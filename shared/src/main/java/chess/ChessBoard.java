package chess;

import java.util.ArrayList;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ArrayList<ArrayList<ChessPiece>> board;

    public ChessBoard() {
        board = new ArrayList<ArrayList<ChessPiece>>();
        for (int i = 0; i < 8; i++) {
            board.add(new ArrayList<ChessPiece>());
        }
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int column = position.getColumn();

        board.get(row).set(column, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();

        return board.get(row).get(column);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            board.get(i).clear();
            if (i == 0) {
                setKingRow(0, ChessGame.TeamColor.WHITE);
            } else if (i == 1) {
                setPawnRow(1, ChessGame.TeamColor.WHITE);
            } else if (i == 6) {
                setPawnRow(6, ChessGame.TeamColor.BLACK);
            } else if (i == 7) {
                setPawnRow(7, ChessGame.TeamColor.BLACK);
            }
        }
    }

    private void setPawnRow(int row, ChessGame.TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }
    }

    private void setKingRow(int row, ChessGame.TeamColor teamColor) {
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        board.get(row).add(new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
    }
}
