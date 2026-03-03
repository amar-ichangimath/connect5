package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable {

    private Socket sender;
    private Socket receiver;
    private final GameBoard boardObj;

    public PlayerHandler(Socket sender, Socket receiver, GameBoard boardObj) {
        this.sender   = sender;
        this.receiver = receiver;
        this.boardObj = boardObj;
    }

    @Override
    public void run() {
        int count = 0;
        BufferedWriter toSender = null, toReceiver = null;
        BufferedReader fromSender = null;

        try {
            toSender   = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
            toReceiver = new BufferedWriter(new OutputStreamWriter(receiver.getOutputStream()));
            fromSender = new BufferedReader(new InputStreamReader(sender.getInputStream()));

            while (true) {
                System.out.println("Waiting for move from: " + sender.getRemoteSocketAddress());
                String input = fromSender.readLine();
                if (input == null) { break; }

                int column;
                try {
                    column = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    toSender.write(Protocol.INVALID + "\n"); toSender.flush();
                    continue;
                }

                int result = boardObj.getResult(column, count);

                if (result == -1) {
                    toSender.write(Protocol.INVALID + "\n"); toSender.flush();
                } else if (result == 1) {
                    sendBoard(toSender, Protocol.WIN);
                    sendBoard(toReceiver, Protocol.LOSE);
                    break;
                } else if (result == 2) {
                    sendBoard(toSender, Protocol.DRAW);
                    sendBoard(toReceiver, Protocol.DRAW);
                    break;
                } else {
                    sendBoard(toSender, Protocol.CONTINUE);
                    sendBoard(toReceiver, Protocol.CONTINUE);
                    count++;
                    Socket temp = sender; sender = receiver; receiver = temp;
                    fromSender = new BufferedReader(new InputStreamReader(sender.getInputStream()));
                    toSender   = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
                    toReceiver = new BufferedWriter(new OutputStreamWriter(receiver.getOutputStream()));
                }
            }
        } catch (Exception e) {
            System.out.println("Game thread error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeQuietly(sender); closeQuietly(receiver);
        }
    }

    private void sendBoard(BufferedWriter out, String code) throws Exception {
        out.write(code + "\n");
        out.write(boardObj.printPattern());
        out.flush();
    }

    private void closeQuietly(Socket s) {
        if (s != null && !s.isClosed()) {
            try { s.close(); } catch (Exception ignored) {}
        }
    }
}
