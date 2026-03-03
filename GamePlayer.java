package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * GamePlayer - console client for Connect5.
 *
 * Key improvements over the original:
 *  - Host and port are configurable via command-line args (default localhost:8080).
 *  - Repeated 6-line board-reading code extracted into readBoard() helper.
 *  - Fixed encoding issue: "It's" was corrupted in the original due to a
 *    non-ASCII apostrophe; replaced with plain ASCII.
 *  - Invalid-move feedback: the server now sends INVALID when a column is full
 *    so the player is prompted to choose again without wasting a turn.
 *  - Draw detection: displays a draw message when the server sends DRAW.
 *  - Protocol strings come from Protocol.java.
 */
public class GamePlayer {

    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 8080;
    private static final int    BOARD_ROWS   = 6;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int    port = DEFAULT_PORT;
        if (args.length > 1) {
            try { port = Integer.parseInt(args[1]); }
            catch (NumberFormatException e) { System.out.println("Invalid port, using " + DEFAULT_PORT); }
        }

        System.out.println("Connecting to Connect5 server at " + host + ":" + port);
        Scanner keyboard = new Scanner(System.in);

        try {
            while (true) {
                playOneGame(host, port, keyboard);

                System.out.print("Play again? (y/n): ");
                if (!"y".equalsIgnoreCase(keyboard.nextLine().trim())) break;
            }
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            keyboard.close();
        }
    }

    private static void playOneGame(String host, int port, Scanner keyboard) throws Exception {
        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // --- Handshake ---
            System.out.print("Enter your name: ");
            String myName = keyboard.nextLine().trim();
            out.write(Protocol.HELLO + myName + "\n");
            out.flush();

            String reply = in.readLine();
            boolean isFirst = Protocol.WAITING.equals(reply);
            if (isFirst) {
                System.out.println("Waiting for an opponent ...");
                reply = in.readLine();
            }

            String opponentName = reply != null ? reply.substring(Protocol.OPPONENT.length()).trim() : "Opponent";
            System.out.println("Opponent: " + opponentName);

            // --- Initial board ---
            printBoard(readBoard(in));

            // --- If second player, wait for first player's opening move ---
            if (!isFirst) {
                System.out.println("Waiting for " + opponentName + "'s move ...");
                String status = in.readLine();
                if (Protocol.WIN.equals(status) || Protocol.LOSE.equals(status) || Protocol.DRAW.equals(status)) {
                    printBoard(readBoard(in));
                    announceResult(status);
                    return;
                }
                printBoard(readBoard(in));
            }

            // --- Main game loop ---
            while (true) {
                // My turn
                int column = promptColumn(keyboard, myName);
                out.write(column + "\n");
                out.flush();

                String status = in.readLine();
                if (Protocol.INVALID.equals(status)) {
                    System.out.println("That column is full! Please choose another.");
                    continue;
                }

                printBoard(readBoard(in));

                if (!Protocol.CONTINUE.equals(status)) {
                    announceResult(status);
                    return;
                }

                // Opponent's turn
                System.out.println("Waiting for " + opponentName + "'s move ...");
                status = in.readLine();
                printBoard(readBoard(in));

                if (!Protocol.CONTINUE.equals(status)) {
                    announceResult(status);
                    return;
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static int promptColumn(Scanner keyboard, String name) {
        while (true) {
            System.out.print("Your turn, " + name + " - enter column (1-9): ");
            try {
                int col = Integer.parseInt(keyboard.nextLine().trim());
                if (col >= 1 && col <= 9) return col;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a number between 1 and 9.");
        }
    }

    /** Reads BOARD_ROWS lines from the server and returns them as one string. */
    private static String readBoard(BufferedReader in) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BOARD_ROWS; i++) {
            sb.append(in.readLine()).append('\n');
        }
        return sb.toString();
    }

    private static void printBoard(String board) {
        System.out.println("----------------------------");
        System.out.print(board);
        System.out.println("----------------------------");
    }

    private static void announceResult(String status) {
        if      (Protocol.WIN.equals(status))  System.out.println("Congratulations! You won!");
        else if (Protocol.LOSE.equals(status)) System.out.println("You lost. Better luck next time!");
        else if (Protocol.DRAW.equals(status)) System.out.println("It's a draw!");
    }
}
