package chess;

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
        resetBoard();
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

    public boolean isValidPosition(ChessPosition position) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        return 0 <= row && row < 8 && 0 <= column && column < 8;
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
        board[row][0] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
        board[row][1] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        board[row][2] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        board[row][3] = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        board[row][4] = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        board[row][5] = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        board[row][6] = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        board[row][7] = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
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
                "board=" + Arrays.toString(board) +
                '}';
    }
}
