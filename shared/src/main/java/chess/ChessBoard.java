package chess;

import chess.ChessPiece.PieceType;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Returns true if given ChessPosition is actually a valid position on a
     * chess board
     *
     * @param position
     * @return true/false
     */
    public static boolean isValidPosition(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return 1 <= row && row <= 8 && 1 <= column && column <= 8;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;

        board[row][column] = piece;
    }

    /**
     * Removes a chess piece to the chessboard
     *
     * @param position where to add the piece to
     */
    public void removePiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;

        board[row][column] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        if (isValidPosition(position)) {
            return board[row][column];
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                setKingRow(0, ChessGame.TeamColor.WHITE);
            } else if (i == 1) {
                setPawnRow(1, ChessGame.TeamColor.WHITE);
            } else if (i == 6) {
                setPawnRow(6, ChessGame.TeamColor.BLACK);
            } else if (i == 7) {
                setKingRow(7, ChessGame.TeamColor.BLACK);
            }
        }
    }

    private void setPawnRow(int row, ChessGame.TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            board[row][i] = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
        }
    }

    private void setKingRow(int row, ChessGame.TeamColor teamColor) {
        PieceType[] kingRow = {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP,
                PieceType.QUEEN, PieceType.KING,
                PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        for (int i = 0; i < kingRow.length; i++) {
            board[row][i] = new ChessPiece(teamColor, kingRow[i]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }
}
