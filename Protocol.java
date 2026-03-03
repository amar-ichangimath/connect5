package connectFivePackage;

/**
 * Protocol – shared string constants used by both the server and the client.
 *
 * Centralising these values means a typo in one place no longer causes a
 * silent mismatch between the two sides of the connection.
 */
public final class Protocol {

    // Client -> Server
    /** Greeting prefix sent by the client, e.g. "HELLO Alice". */
    public static final String HELLO    = "HELLO ";

    // Server -> Client (handshake)
    /** Sent to player 1 to indicate they should wait for an opponent. */
    public static final String WAITING  = "100";
    /** Sent to both players once matched, followed by the opponent's name. */
    public static final String OPPONENT = "200";

    // Server -> Client (in-game)
    /** Board updated; game continues. */
    public static final String CONTINUE = "0";
    /** The player who just moved has won. */
    public static final String WIN      = "1";
    /** The other player has won (you lost). */
    public static final String LOSE     = "2";
    /** The game is a draw. */
    public static final String DRAW     = "3";
    /** The chosen column was full; please try again. */
    public static final String INVALID  = "4";

    private Protocol() {}   // utility class - no instantiation
}
