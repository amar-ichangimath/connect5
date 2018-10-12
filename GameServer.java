package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
	public static void main(String[] args) {
		
		// Declare Required Variables
		int port=8080;
		Socket player1 = null, player2 = null;
	    PlayerHandler handler=null;
	    String firstPlayer = "", secondPlayer = "", input = "";
	    ServerSocket serverSock = null;
	    BufferedReader player1Input = null, player2Input = null;
	    BufferedWriter player1Output = null, player2Output = null;
	    
	    try {
	    	System.out.println("Waiting for the Players to Connect....");
	        serverSock = new ServerSocket(port);
	        
	        while(true) {
	        // Connect Player 1
	        player1 = serverSock.accept();
	        player1Input = new BufferedReader(new InputStreamReader(player1.getInputStream()));
	        player1Output= new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()));
	        input = player1Input.readLine();
	        
	        if(input.startsWith("HELLO")){
	            player1Output.write("100\n"); // When the First Player Connects to the Server
	            player1Output.flush();
	            firstPlayer=input.substring(6);
	            System.out.println("Player 1, "+firstPlayer+" is now connected. Waiting for opponent....");
	            
	            // Connect Player 2
	            player2 = serverSock.accept(); // Server Accepting Connections
	            player2Input = new BufferedReader(new InputStreamReader(player2.getInputStream()));
	            player2Output= new BufferedWriter(new OutputStreamWriter(player2.getOutputStream()));

	            input=player2Input.readLine();
	            secondPlayer=input.substring(6);
	            
	            player1Output.write("200 "+secondPlayer+"\n");
	            player1Output.flush();
	            player2Output.write("200 "+firstPlayer+"\n");
	            player2Output.flush();

	            // Pair the Players for Game
	            System.out.println(firstPlayer+" v/s "+secondPlayer);
	            GameBoard boardObj = new GameBoard(); // GameBoard object
	            player1Output.write(boardObj.printPattern());
	            player1Output.flush();
	            player2Output.write(boardObj.printPattern());
	            player2Output.flush();

	            handler = new PlayerHandler(player1, player2, boardObj); // PlayerHandler object initialized
	            Thread thread = new Thread(handler); // Thread created
	            thread.start();
	        }
	       }
	    }catch(Exception e) {
	    	System.out.println("An Exception Occurred:: "+e.getMessage());
	    	e.printStackTrace();
	    }
	}

}
