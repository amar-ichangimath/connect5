package connectFivePackage;

/*import java.util.Arrays;
import java.util.Scanner;
import java.util.StringJoiner;
*/
public class GameBoard {
	
	// Declare Required Variables
	private final int HEIGHT = 6;
	private final int WIDTH = 27;
	String[][] board;
	
	// Constructor for the GameBoard class
	public GameBoard() {
		board = new String[HEIGHT][WIDTH];
		board = createPattern();
	}

	// Method to create the board pattern as specified
	public String[][] createPattern(){
		try {
			String[][] board = new String[HEIGHT][WIDTH];

			// Iterating through the game grid to form the pattern
			for (int i =0;i<board.length;i++)
			{
				for (int j =0;j<board[i].length;j++)
				{
					// Placing appropriate symbols to create the pattern
					if(j % 3 == 1) board[i][j] =" ";
					if(j % 3 == 0) board[i][j] ="[";
					if(j % 3 == 2) board[i][j] ="]";
				}	

			}
			return board;
		} catch (Exception e) {
			System.out.println("An Exception Occurred:: "+e);
			e.printStackTrace();
		}
		return null;
	}
	
	// Method to print the board pattern
	public String printPattern() {
		StringBuilder builder = new StringBuilder();
		try {
			for (int i =0;i<board.length;i++)
			{
				for (int j=0;j<board[i].length;j++)
				{
					builder.append(board[i][j]+"");
				}
				builder.append("\n");
			}
		} catch (Exception e) {
			System.out.println("An Exception Occurred:: "+e);
			e.printStackTrace();
		}
		return builder.toString();
	}

	// Method to drop the "x" disc in the appropriate column
	public int dropXDisc(int columnNum) {
		try {
			int column;
			switch(columnNum) {
			case 1:
				column = 1;
				break;
			case 2:
				column = 4;
				break;
			case 3:
				column = 7;
				break;
			case 4:
				column = 10;
				break;
			case 5:
				column = 13;
				break;
			case 6:
				column = 16;
				break;
			case 7:
				column = 19;
				break;
			case 8:
				column = 22;
				break;
			case 9:
				column = 25;
				break;
			default:
				column = 0;
				break;
			}
			for (int i=5;i>=0;i--)
			{
				if (board[i][column] == " ")
				{
					board[i][column] = "x";
					break;
				}

			}
			return 1;
		} catch (Exception e) {
			System.out.println("An Exception Occurred:: "+e);
			e.printStackTrace();
		}
		return 0;

	}
	
	// Method to drop the "o" disc in the appropriate column
	public int dropODisc(int columnNum) {
		try {
			int column;
			switch(columnNum) {
			case 1:
				column = 1;
				break;
			case 2:
				column = 4;
				break;
			case 3:
				column = 7;
				break;
			case 4:
				column = 10;
				break;
			case 5:
				column = 13;
				break;
			case 6:
				column = 16;
				break;
			case 7:
				column = 19;
				break;
			case 8:
				column = 22;
				break;
			case 9:
				column = 25;
				break;
			default:
				column = 0;
				break;
			}
			for (int i=5;i>=0;i--)
			{
				if (board[i][column] == " ")
				{
					board[i][column] = "o";
					break;
				}

			}
			return 1;
		} catch (Exception e) {
			System.out.println("An Exception Occurred:: "+e);
			e.printStackTrace();
		}
		return 0;

	}
	
	// Method to check for Winner
	public String checkWinner() {
		try {
			// Horizontal
			for (int i =0;i<6;i++)
			{
				for (int j=0;j<13;j+=3)
				{
					if ((board[i][j+1] != " ")
							&& (board[i][j+4] != " ")
							&& (board[i][j+7] != " ")
							&& (board[i][j+10] != " ")
							&& (board[i][j+13] != " ")
							&& ((board[i][j+1] == board[i][j+4])
							&& (board[i][j+4] == board[i][j+7])
							&& (board[i][j+7] == board[i][j+10])
							&& (board[i][j+10] == board[i][j+13])))
						    return board[i][j+1];  
				}
			}

			// Vertical
			for (int i=1;i<26;i+=3)
			{
				for (int j=0;j<2;j++)
				{
					if((board[j][i] != " ")
							&& (board[j+1][i] != " ")
							&& (board[j+2][i] != " ")
							&& (board[j+3][i] != " ")
							&& (board[j+4][i] != " ")
							&& ((board[j][i] == board[j+1][i])
							&& (board[j+1][i] == board[j+2][i])
							&& (board[j+2][i] == board[j+3][i])
							&& (board[j+3][i] == board[j+4][i])))
						    return board[j][i]; 
				}
			}

			// Left-Up to Top-Right-Down Diagonal
			for (int i=0;i<2;i++)
			{
				for (int j=1;j<6;j+=3)
				{
					if ((board[i][j] != " ")
							&& (board[i+1][j+3] != " ")
							&& (board[i+2][j+6] != " ")
							&& (board[i+3][j+9] != " ")
							&& (board[i+4][j+12] != " ")
							&& (board[i][j] == board[i+1][j+3])
							&& (board[i+1][j+3] == board[i+2][j+6]
							&& (board[i+2][j+6] == board[i+3][j+9])
							&& (board[i+3][j+9] == board[i+4][j+12])))
						    return board[i][j]; 
				}
			}

			// Right-Up to Bottom-Left-Down Diagonal
			for (int i=0;i<2;i++)
			{
				for (int j=25;j>6;j-=3)
				{
					if ((board[i][j] != " ")
							&& (board[i+1][j-3] != " ")
							&& (board[i+2][j-6] != " ")
							&& (board[i+3][j-9] != " ")
							&& (board[i+4][j-12] != " ")
							&& (board[i][j] == board[i+1][j-3])
							&& (board[i+1][j-3] == board[i+2][j-6]
							&& (board[i+2][j-6] == board[i+3][j-9])
							&& (board[i+3][j-9] == board[i+4][j-12])))
						    return board[i][j]; 
				}
			}
		} catch (Exception e) {
			System.out.println("An Exception Occurred:: "+e);
			e.printStackTrace();
		}
		return null;
	}

	// Method to get the appropriate result that can be passed down to the player
	public int getResult (int column, int count) {
		if (count % 2 == 0) dropXDisc(column);
		else dropODisc(column);
		if (checkWinner() != null)
		{
			if (checkWinner() == "x") {
				System.out.println("The x player won...");
				return 1;
			}
			else if (checkWinner() == "o") {
				System.out.println("The o player won...");
				return 1;
			}
		}
		return 0;
	}

}
