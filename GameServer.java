package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * GameServer - listens for two players on a configurable port, pairs them,
 * and hands the game off to a dedicated PlayerHandler thread.
 *
 * Key improvements over the original:
 *  - Port is configurable via a command-line argument (default 8080).
 *  - ServerSocket is opened with try-with-resources so it closes on exit.
 *  - Pairing logic is extracted into a helper method for clarity.
 *  - Graceful shutdown: Ctrl-C closes the server socket cleanly.
 *  - Protocol strings are centralised in Protocol.java.
 */
public class GameServer {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = parsePort(args);
        System.out.println("Connect5 Server starting on port " + port + " ...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { serverSocket.close(); } catch (Exception ignored) {}
                System.out.println("Server shut down.");
            }));

            System.out.println("Waiting for players to connect ...");

            while (!serverSocket.isClosed()) {
                acceptAndPairPlayers(serverSocket);
            }

        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Blocks until two players have connected and completed the HELLO handshake,
     * then starts a PlayerHandler thread for their game.
     */
    private static void acceptAndPairPlayers(ServerSocket serverSocket) {
        try {
            // --- Player 1 ---
            Socket p1Socket = serverSocket.accept();
            BufferedReader p1In  = new BufferedReader(new InputStreamReader(p1Socket.getInputStream()));
            BufferedWriter p1Out = new BufferedWriter(new OutputStreamWriter(p1Socket.getOutputStream()));

            String p1Msg = p1In.readLine();
            if (p1Msg == null || !p1Msg.startsWith(Protocol.HELLO)) {
                p1Socket.close();
                return;
            }
            String player1Name = p1Msg.substring(Protocol.HELLO.length()).trim();
            p1Out.write(Protocol.WAITING + "\n");
            p1Out.flush();
            System.out.println("Player 1 (" + player1Name + ") connected. Waiting for opponent ...");

            // --- Player 2 ---
            Socket p2Socket = serverSocket.accept();
            BufferedReader p2In  = new BufferedReader(new InputStreamReader(p2Socket.getInputStream()));
            BufferedWriter p2Out = new BufferedWriter(new OutputStreamWriter(p2Socket.getOutputStream()));

            String p2Msg = p2In.readLine();
            if (p2Msg == null || !p2Msg.startsWith(Protocol.HELLO)) {
                p2Socket.close();
                p1Socket.close();
                return;
            }
            String player2Name = p2Msg.substring(Protocol.HELLO.length()).trim();

            // Notify both players of their opponent
            p1Out.write(Protocol.OPPONENT + " " + player2Name + "\n");
            p1Out.flush();
            p2Out.write(Protocol.OPPONENT + " " + player1Name + "\n");
            p2Out.flush();

            System.out.println("Match started: " + player1Name + " vs " + player2Name);

            // Send the initial board to both players
            GameBoard board = new GameBoard();
            String initialBoard = board.printPattern();
            p1Out.write(initialBoard);
            p1Out.flush();
            p2Out.write(initialBoard);
            p2Out.flush();

            // Delegate game loop to PlayerHandler on its own thread
            PlayerHandler handler = new PlayerHandler(p1Socket, p2Socket, board);
            new Thread(handler, player1Name + "-vs-" + player2Name).start();

        } catch (Exception e) {
            System.out.println("Error pairing players: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int parsePort(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port argument; using default " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }
}
