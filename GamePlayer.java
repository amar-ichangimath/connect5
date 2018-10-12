package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class GamePlayer {
	public static void main(String[] args) {
		
		// Declare Required Variables
		Scanner keyboard;
	    String userInput, serverReply="", myUsername, opUsername;
	    boolean isFirst;
	    String host = "localhost";
	    int port=8080;
	    Socket connectionSocket;
	    BufferedWriter serverOutput;
	    BufferedReader serverInput;
	    System.out.println("Connecting to the Game Server on Port: "+port);
	    
	    try {
	    	while(true) {
	    		keyboard = new Scanner(System.in);
	    		connectionSocket=new Socket(host, port); // Connected to the Server
	            serverOutput= new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
	            serverInput= new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	            System.out.println("Connection Established!\nWelcome to the Game!!");
	            System.out.print("Please Enter your Name: ");
	            myUsername = keyboard.nextLine();
	            userInput = "HELLO "+myUsername+"\n";
	            serverOutput.write(userInput);
	            serverOutput.flush();
	            serverReply = serverInput.readLine();
	            isFirst=false;
	            
	            // Based on the Server Replies, the validations as well as operations are carried out
	            if(serverReply.equals("100")){
	              isFirst=true;
	              System.out.println("Waiting for the Opponent...");
	              serverReply=serverInput.readLine();
	            }
	            opUsername=serverReply.substring(4);
	            System.out.println("Your opponent will be: "+opUsername);
	            serverReply = serverInput.readLine();
	            serverReply +="\n";
	            serverReply += serverInput.readLine();
	            serverReply +="\n";
	            serverReply += serverInput.readLine();
	            serverReply +="\n";
	            serverReply +=serverInput.readLine();
	            serverReply +="\n";
	            serverReply +=serverInput.readLine();
	            serverReply +="\n";
	            serverReply +=serverInput.readLine();
	            serverReply +="\n";
	            System.out.println("----------------------------");
	            System.out.print(serverReply);
	            System.out.println("----------------------------");
	            
	            if(!isFirst){
	                System.out.println("Waiting for "+opUsername+"\'s move...");
	                serverReply=serverInput.readLine();
	                serverReply = serverInput.readLine();
		            serverReply +="\n";
		            serverReply += serverInput.readLine();
		            serverReply +="\n";
		            serverReply += serverInput.readLine();
		            serverReply +="\n";
		            serverReply +=serverInput.readLine();
		            serverReply +="\n";
		            serverReply +=serverInput.readLine();
		            serverReply +="\n";
		            serverReply +=serverInput.readLine();
		            serverReply +="\n";
		            System.out.println("----------------------------");
		            System.out.print(serverReply);
		            System.out.println("----------------------------");
	              }
	            
	            while(true) {
	            	System.out.print("It’s your turn "+myUsername+", please enter column (1-9): ");
	                userInput= keyboard.nextLine();
	                serverOutput.write(userInput+"\n");
	                serverOutput.flush();
	                
	                serverReply=serverInput.readLine();
	                if(serverReply.equals("0")){
	                	serverReply = serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            System.out.println("----------------------------");
			            System.out.print(serverReply);
			            System.out.println("----------------------------");
	                  }else if(serverReply.equals("1")){
	                	  serverReply = serverInput.readLine();
				            serverReply +="\n";
				            serverReply += serverInput.readLine();
				            serverReply +="\n";
				            serverReply += serverInput.readLine();
				            serverReply +="\n";
				            serverReply +=serverInput.readLine();
				            serverReply +="\n";
				            serverReply +=serverInput.readLine();
				            serverReply +="\n";
				            serverReply +=serverInput.readLine();
				            serverReply +="\n";
	                      System.out.println("----------------------------");
	                      System.out.println(serverReply);
	                      System.out.println("----------------------------");
	                      System.out.println("Congratulations! You won the game!");
	                      break;
	                    }
	                
	                System.out.println("Waiting for "+opUsername+"\'s move...");
	                serverReply=serverInput.readLine();
	                if(serverReply.equals("0")){
	                	serverReply = serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            System.out.println("----------------------------");
			            System.out.print(serverReply);
			            System.out.println("----------------------------");
	                  }else {
	                	serverReply = serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply += serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            serverReply +=serverInput.readLine();
			            serverReply +="\n";
			            System.out.println("----------------------------");
			            System.out.print(serverReply);
			            System.out.println("----------------------------");
			            System.out.println("Sorry, you lost the game. Better luck next time!");
	                    break;
	                }
	            }
	            connectionSocket.close();
	            serverInput.close();
	            serverOutput.close();
	            
	            // A minor modification so that the client can continue playing the game
	            System.out.print("Do you want to play the game once again? (y/n): ");
	            userInput= keyboard.nextLine();
	            if(userInput.equals("n")) break;
	    	}
	    }catch(Exception e) {
	    	System.out.println("An Exception Occurred:: "+e.getMessage());
	    	e.printStackTrace();
	    }
	}
	
	

}
