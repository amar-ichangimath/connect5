package connectFivePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable {
	
	private Socket sender = null, receiver=null;
	private GameBoard boardObj = null;

	PlayerHandler(Socket sender, Socket receiver, GameBoard boardObj)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.boardObj = boardObj;
	}

	@Override
	public void run() {
		Socket temp=null;
		String input="";
		int column = 0, count=0, result=0;
		BufferedWriter outputToSender=null, outputToReceiver=null;
		BufferedReader clientInput=null;
		
		try {
			while(true) {
				// Get the output to sender and to receiver
				outputToSender = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
				outputToReceiver = new BufferedWriter(new OutputStreamWriter(receiver.getOutputStream()));
				clientInput = new BufferedReader(new InputStreamReader(sender.getInputStream()));
				
				// Connection made with p layer
				System.out.println("Connection made with " + sender);
				input = clientInput.readLine();
				System.out.println("Column Number Received: " + input);
				try{
					column = Integer.parseInt(input);
				}
				catch(NumberFormatException e){
					System.out.println("An Exception Occurred:: "+e.getMessage());
			    	e.printStackTrace();
				}
				
				result = boardObj.getResult(column, count);
				count++;
				System.out.println("Result: "+result);
				// When the result is 1, player wins
				if(result==1){
					System.out.println(sender+" has won the game...");
					outputToSender.write("1\n");
					outputToSender.flush();
					outputToSender.write(boardObj.printPattern());
					outputToSender.flush();
					outputToReceiver.write("1\n");
					outputToReceiver.flush();
					outputToReceiver.write(boardObj.printPattern());
					outputToReceiver.flush();
					outputToReceiver.close();
					outputToSender.close();
					clientInput.close();
					sender.close();
					receiver.close();
					break;
				}
				else{
				    //Updated board message
					System.out.println("Updated the board according to the changes...");
					outputToSender.write("0\n");
					outputToSender.flush();
					outputToSender.write(boardObj.printPattern());
					outputToSender.flush();
					outputToReceiver.write("0\n");
					outputToReceiver.flush();
					outputToReceiver.write(boardObj.printPattern());
					outputToReceiver.flush();
					System.out.println("Switching between sender and reciever...");
					temp=sender;
					sender=receiver;
					receiver=temp;
				}
				
			}
		}catch(Exception e) {
			System.out.println("An Exception Occurred:: "+e.getMessage());
	    	e.printStackTrace();
		}
		
	}
}
