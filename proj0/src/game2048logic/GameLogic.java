package game2048logic;

import game2048rendering.Side;
import static game2048logic.MatrixUtils.rotateLeft;
import static game2048logic.MatrixUtils.rotateRight;

/**
 * @author  Josh Hug
 */
public class GameLogic {
    /** Moves the given tile up as far as possible, subject to the minR constraint.
     *
     * @param board the current state of the board
     * @param r     the row number of the tile to move up
     * @param c -   the column number of the tile to move up
     * @param minR  the minimum row number that the tile can land in, e.g.
     *              if minR is 2, the moving tile should move no higher than row 2.
     * @return      if there is a merge, returns the 1 + the row number where the merge occurred.
     *              if no merge occurs, then return 0.
     */
    public static int moveTileUpAsFarAsPossible(int[][] board, int r, int c, int minR) {
        int currentRow = r;
        while (currentRow > minR) {
            int nextRow = currentRow - 1;
            if (board[nextRow][c] == 0) {
                board[nextRow][c] = board[currentRow][c];
                board[currentRow][c] = 0;
                currentRow = nextRow;
            } else if (board[nextRow][c] == board[currentRow][c]) {
                board[nextRow][c] *= 2;
                board[currentRow][c] = 0;
                return nextRow + 1;
            } else {
                break;
            }
        }
        return 0;
    }

    /**
     * Modifies the board to simulate the process of tilting column c
     * upwards.
     *
     * @param board     the current state of the board
     * @param c         the column to tilt up.
     */
    public static void tiltColumn(int[][] board, int c) {
        // Process tiles from top to bottom. To make sure a tile that has
        // already been merged this tilt isn't merged again, we temporarily
        // mark merged tiles with a negative value and fix the signs at the
        // end. moveTileUpAsFarAsPossible only merges tiles of equal value,
        // so negative (already merged) tiles are never merged into.
        for (int r = 0; r < board.length; r++) {
            if (board[r][c] != 0) {
                int mergedRow = moveTileUpAsFarAsPossible(board, r, c, 0);
                if (mergedRow > 0) {
                    board[mergedRow - 1][c] = -board[mergedRow - 1][c];
                }
            }
        }
        for (int r = 0; r < board.length; r++) {
            if (board[r][c] < 0) {
                board[r][c] = -board[r][c];
            }
        }
    }

    /**
     * Modifies the board to simulate tilting all columns upwards.
     *
     * @param board     the current state of the board.
     */
    public static void tiltUp(int[][] board) {
        for (int c = 0; c < board.length; c++) {
            tiltColumn(board, c);
        }
    }

    /**
     * Modifies the board to simulate tilting the entire board to
     * the given side.
     *
     * @param board the current state of the board
     * @param side  the direction to tilt
     */
    public static void tilt(int[][] board, Side side) {
        if (side == Side.EAST) {
            rotateLeft(board);
            tiltUp(board);
            rotateRight(board);
        } else if (side == Side.WEST) {
            rotateRight(board);
            tiltUp(board);
            rotateLeft(board);
        } else if (side == Side.SOUTH) {
            rotateLeft(board);
            rotateLeft(board);
            tiltUp(board);
            rotateRight(board);
            rotateRight(board);
        } else {
            tiltUp(board);
        }
    }
}
