package connectFivePackage;

/**
 * GameBoard class – manages the Connect5 grid.
 *
 * Key improvements over the original:
 *  - Merged duplicate dropXDisc/dropODisc into a single dropDisc(col, disc).
 *  - Fixed String comparison: replaced == with .equals() throughout.
 *  - Added isColumnFull() guard so discs are never silently dropped into a
 *    full column.
 *  - Added isBoardFull() to support draw detection.
 *  - getResult() now returns a richer status code:
 *      0 = move accepted, game continues
 *      1 = the player who just moved has won
 *      2 = board is full – draw
 *     -1 = column was full, move rejected
 *  - Replaced a raw switch on column number with a simple formula
 *    (colIndex = (columnNum - 1) * 3 + 1) to eliminate repetition.
 *  - Javadoc added to every public method.
 */
public class GameBoard {

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    private static final int HEIGHT        = 6;
    private static final int WIDTH         = 27;
    private static final int NUM_COLUMNS   = 9;
    private static final int WIN_LENGTH    = 5;

    /** Token used by the first player. */
    public static final String DISC_X = "x";
    /** Token used by the second player. */
    public static final String DISC_O = "o";

    // ---------------------------------------------------------------
    // State
    // ---------------------------------------------------------------
    private final String[][] board;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------
    public GameBoard() {
        board = createPattern();
    }

    // ---------------------------------------------------------------
    // Board initialisation
    // ---------------------------------------------------------------

    /**
     * Builds the initial empty board with the bracket pattern.
     *
     * @return a freshly initialised 2-D String array
     */
    private String[][] createPattern() {
        String[][] b = new String[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if      (j % 3 == 0) b[i][j] = "[";
                else if (j % 3 == 2) b[i][j] = "]";
                else                 b[i][j] = " ";
            }
        }
        return b;
    }

    // ---------------------------------------------------------------
    // Board rendering
    // ---------------------------------------------------------------

    /**
     * Returns the current board as a printable String.
     *
     * @return multi-line board representation
     */
    public String printPattern() {
        StringBuilder sb = new StringBuilder();
        for (String[] row : board) {
            for (String cell : row) sb.append(cell);
            sb.append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------
    // Column helpers
    // ---------------------------------------------------------------

    /**
     * Converts a 1-based column number (1–9) to its board index.
     *
     * @param columnNum 1-based column number
     * @return internal board column index, or -1 if out of range
     */
    private int toBoardIndex(int columnNum) {
        if (columnNum < 1 || columnNum > NUM_COLUMNS) return -1;
        return (columnNum - 1) * 3 + 1;
    }

    /**
     * Returns {@code true} when every cell in the given column is occupied.
     *
     * @param columnNum 1-based column number
     * @return whether the column is full
     */
    public boolean isColumnFull(int columnNum) {
        int col = toBoardIndex(columnNum);
        if (col == -1) return true;          // treat invalid column as "full"
        return !board[0][col].equals(" ");
    }

    /**
     * Returns {@code true} when no empty cells remain anywhere on the board.
     *
     * @return whether the board is completely full
     */
    public boolean isBoardFull() {
        for (int col = 1; col <= NUM_COLUMNS; col++) {
            if (!isColumnFull(col)) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------
    // Disc placement (unified method replaces the original pair)
    // ---------------------------------------------------------------

    /**
     * Drops a disc into the specified column.
     * The disc falls to the lowest available row (gravity simulation).
     *
     * @param columnNum 1-based column number (1–9)
     * @param disc      the token to place ({@code "x"} or {@code "o"})
     * @return {@code true} if the disc was placed, {@code false} if the column
     *         was full or the column number was invalid
     */
    public boolean dropDisc(int columnNum, String disc) {
        int col = toBoardIndex(columnNum);
        if (col == -1 || isColumnFull(columnNum)) return false;

        for (int row = HEIGHT - 1; row >= 0; row--) {
            if (board[row][col].equals(" ")) {
                board[row][col] = disc;
                return true;
            }
        }
        return false;   // should be unreachable after isColumnFull check
    }

    // ---------------------------------------------------------------
    // Win detection
    // ---------------------------------------------------------------

    /**
     * Checks the board for a winning sequence of {@value #WIN_LENGTH}.
     * Checks horizontal, vertical, and both diagonal directions.
     *
     * @return the winning disc token ({@code "x"} or {@code "o"}),
     *         or {@code null} if no winner yet
     */
    public String checkWinner() {
        // Horizontal
        for (int row = 0; row < HEIGHT; row++) {
            for (int startCol = 1; startCol <= WIDTH - (WIN_LENGTH - 1) * 3; startCol += 3) {
                String winner = checkSequence(row, startCol, 0, 3);
                if (winner != null) return winner;
            }
        }

        // Vertical
        for (int col = 1; col < WIDTH; col += 3) {
            for (int startRow = 0; startRow <= HEIGHT - WIN_LENGTH; startRow++) {
                String winner = checkSequence(startRow, col, 1, 0);
                if (winner != null) return winner;
            }
        }

        // Diagonal: top-left to bottom-right
        for (int row = 0; row <= HEIGHT - WIN_LENGTH; row++) {
            for (int col = 1; col <= WIDTH - (WIN_LENGTH - 1) * 3; col += 3) {
                String winner = checkSequence(row, col, 1, 3);
                if (winner != null) return winner;
            }
        }

        // Diagonal: top-right to bottom-left
        for (int row = 0; row <= HEIGHT - WIN_LENGTH; row++) {
            for (int col = (WIN_LENGTH - 1) * 3 + 1; col < WIDTH; col += 3) {
                String winner = checkSequence(row, col, 1, -3);
                if (winner != null) return winner;
            }
        }

        return null;
    }

    /**
     * Checks a single run of {@value #WIN_LENGTH} cells starting at
     * (startRow, startCol) and stepping by (rowStep, colStep).
     *
     * @return the disc token if all cells match, otherwise {@code null}
     */
    private String checkSequence(int startRow, int startCol, int rowStep, int colStep) {
        String first = board[startRow][startCol];
        if (first.equals(" ")) return null;

        for (int k = 1; k < WIN_LENGTH; k++) {
            int r = startRow + k * rowStep;
            int c = startCol + k * colStep;
            if (r < 0 || r >= HEIGHT || c < 0 || c >= WIDTH) return null;
            if (!board[r][c].equals(first)) return null;
        }
        return first;
    }

    // ---------------------------------------------------------------
    // Move processing
    // ---------------------------------------------------------------

    /**
     * Processes a move for the current turn.
     *
     * @param column 1-based column number chosen by the player
     * @param count  number of moves made so far (even = X's turn, odd = O's)
     * @return <ul>
     *           <li>{@code  0} – move OK, game continues</li>
     *           <li>{@code  1} – move resulted in a win</li>
     *           <li>{@code  2} – board is now full (draw)</li>
     *           <li>{@code -1} – column full or invalid, move rejected</li>
     *         </ul>
     */
    public int getResult(int column, int count) {
        String disc = (count % 2 == 0) ? DISC_X : DISC_O;

        if (!dropDisc(column, disc)) {
            System.out.println("Column " + column + " is full or invalid.");
            return -1;
        }

        if (checkWinner() != null) {
            System.out.println("Winner: " + disc);
            return 1;
        }

        if (isBoardFull()) {
            System.out.println("Board is full – draw.");
            return 2;
        }

        return 0;
    }
}
