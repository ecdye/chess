package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @param board
     * @param myPosition
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {
            return moves;
        }

        // Handle the special case of pawns separately
        if (piece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }

        int[] directions = getDirections(piece.getPieceType());
        for (int i = 0; i < directions.length; i += 2) {
            int row = myPosition.getRow() + directions[i];
            int col = myPosition.getColumn() + directions[i + 1];
            ChessGame.TeamColor color = piece.getTeamColor();
            PieceType type = piece.getPieceType();

            ChessPosition newPosition = new ChessPosition(row, col);
            if (!ChessBoard.isValidPosition(newPosition)) {
                continue;
            }

            ChessPiece newPiece = board.getPiece(newPosition);
            if (newPiece != null && newPiece.getTeamColor() == color) {
                continue;
            }

            moves.add(new ChessMove(myPosition, newPosition, null));

            // Handle special cases for pieces that can move multiple squares
            if (type == PieceType.QUEEN || type == PieceType.BISHOP || type == PieceType.ROOK) {
                while (newPiece == null) {
                    row += directions[i];
                    col += directions[i + 1];

                    newPosition = new ChessPosition(row, col);
                    if (!ChessBoard.isValidPosition(newPosition)) {
                        break;
                    }

                    newPiece = board.getPiece(newPosition);
                    if (newPiece != null && newPiece.getTeamColor() == color) {
                        break;
                    }

                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        return moves;
    }

    /**
     * Returns the directions a piece can move in based on its type
     *
     * @param pieceType The type of piece to get directions for
     * @return An array of directions in the form of {row1, col1, row2, col2, ...}
     */
    private int[] getDirections(PieceType pieceType) {
        switch (pieceType) {
            case KING:
            case QUEEN:
                return new int[]{0, 1, 0, -1, 1, 0, 1, 1, 1, -1, -1, 0, -1, 1, -1, -1};
            case BISHOP:
                return new int[]{1, 1, 1, -1, -1, 1, -1, -1};
            case KNIGHT:
                return new int[]{2, 1, 2, -1, -2, 1, -2, -1, 1, 2, 1, -2, -1, 2, -1, -2};
            case ROOK:
                return new int[]{0, 1, 0, -1, 1, 0, -1, 0};
            default:
                return new int[0];
        }
    }

    /**
     * Calculates all the positions a pawn can move to
     *
     * @param board
     * @param myPosition
     * @return Collection of valid moves
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();

        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(row, col);

        // Handle basic move forward
        if (ChessBoard.isValidPosition(newPosition) && board.getPiece(newPosition) == null) {
            if (row == 8 || row == 1) {
                addPromotions(moves, myPosition, newPosition);
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        // Handle double move forward
        if ((color == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                (color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
            ChessPosition intermediatePosition = new ChessPosition(row, col);
            newPosition = new ChessPosition(row + direction, col);
            if (board.getPiece(intermediatePosition) == null && board.getPiece(newPosition) == null) {
                row += direction;
                newPosition = new ChessPosition(row, col);
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        // Handle diagonal moves when capturing
        int[] directions = new int[]{1, -1};
        for (int diagonal : directions) {
            newPosition = new ChessPosition(myPosition.getRow() + direction, col + diagonal);
            ChessPiece newPiece = board.getPiece(newPosition);
            if (newPiece != null && newPiece.getTeamColor() != color) {
                if (newPosition.getRow() == 8 || newPosition.getRow() == 1) {
                    addPromotions(moves, myPosition, newPosition);
                } else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        return moves;
    }

    /**
     * Adds all possible promotions for a pawn to the list of possible moves
     *
     * @param moves Collection of moves to add to lol
     * @param from  Current position of the pawn
     * @param to    Position the pawn is moving to
     */
    private void addPromotions(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        PieceType[] promotions = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        for (PieceType promotion : promotions) {
            moves.add(new ChessMove(from, to, promotion));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", color=" + color +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
}
