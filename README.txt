# Connect5 - Improved Java Implementation

A networked two-player variation of Connect Four. First player to align five
consecutive discs (horizontal, vertical, or diagonal) wins.

## Project Structure

  src/connectFivePackage/
    Protocol.java       -- Shared protocol constants (NEW)
    GameBoard.java      -- Board state, disc placement, win detection
    GameServer.java     -- Server: accepts players and starts game threads
    PlayerHandler.java  -- Per-game thread managing turn logic
    GamePlayer.java     -- Console client

## Compile

  javac -d out src/connectFivePackage/*.java

## Run

  Server (default port 8080):
    java -cp out connectFivePackage.GameServer
  
  Custom port:
    java -cp out connectFivePackage.GameServer 9090

  Client (default localhost:8080):
    java -cp out connectFivePackage.GamePlayer
  
  Custom host/port:
    java -cp out connectFivePackage.GamePlayer localhost 9090

Launch two player clients to start a game.

## Bug Fixes

1. String comparison: All == on String replaced with .equals() throughout.
   This was a critical silent bug causing win detection to always fail.

2. Diagonal detection: Original only checked 2 starting rows; improved
   checkSequence() helper covers all valid positions on the full board.

3. Win/loss symmetry: Original sent "1" to both players on a win; now the
   mover gets WIN (1) and their opponent gets LOSE (2).

4. Resource leak: Streams were recreated each loop iteration in PlayerHandler;
   now initialised once and closed properly in a finally block.

## Refactoring

- dropXDisc and dropODisc merged into dropDisc(col, disc).
- 9-case column switch replaced by formula: (col - 1) * 3 + 1.
- Board-reading code extracted into readBoard() in GamePlayer (was copy-pasted
  8+ times in the original).
- Protocol strings centralised in Protocol.java.

## New Features

- Draw detection via isBoardFull().
- Column-full guard via isColumnFull(); invalid moves prompt a retry (code 4).
- Configurable host/port for server and client via command-line arguments.
- Graceful server shutdown on Ctrl-C.
- Local input validation in GamePlayer before sending to server.
