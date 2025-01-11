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
    private PieceType type;
    private final ChessGame.TeamColor color;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
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
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {
            return moves;
        }
        PieceType pieceType = piece.getPieceType();
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        int[] directions;
        switch (pieceType) {
            case KING:
                directions = new int[] {
                        0, 1,    // right
                        0, -1,   // left
                        1, 0,    // down
                        1, 1,    // down-right
                        1, -1,    // down-left
                        -1, 0,   // up
                        -1, 1,   // up-right
                        -1, -1   // up-left
                };
                break;
            case QUEEN:
                directions = new int[] {
                        0, 1,    // right
                        0, -1,   // left
                        1, 0,    // down
                        -1, 0,   // up
                        1, 1,    // down-right
                        1, -1,    // down-left
                        -1, 1,   // up-right
                        -1, -1   // up-left
                };
                break;
            case BISHOP:
                directions = new int[] {
                        1, 1,    // down-right
                        1, -1,    // down-left
                        -1, 1,   // up-right
                        -1, -1   // up-left
                };
                break;
            case KNIGHT:
                directions = new int[] {
                        2, 1,
                        2, -1,
                        -2, 1,
                        -2, -1,
                        1, 2,
                        1, -2,
                        -1, 2,
                        -1, -2
                };
                break;
            case ROOK:
                directions = new int[] {
                        0, 1,    // right
                        0, -1,   // left
                        1, 0,    // down
                        -1, 0    // up
                };
                break;
            case PAWN:
                return pawnMoves(board, myPosition);
            default:
                return moves;
        }

        for (int i = 0; i < directions.length; i += 2) {
            int newRow = myPosition.getRow() + directions[i];
            int newColumn = myPosition.getColumn() + directions[i + 1];
            ChessPosition newPosition = new ChessPosition(newRow, newColumn);

            if (!board.isValidPosition(newPosition)) {
                continue;
            }

            ChessPiece newPiece = board.getPiece(newPosition);
            if (newPiece != null && newPiece.getTeamColor() == teamColor) {
                continue;
            }

            ChessMove validMove = new ChessMove(myPosition, newPosition, null);
            moves.add(validMove);

            if (pieceType == PieceType.QUEEN || pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK) {
                while (newPiece == null) {
                    newRow += directions[i];
                    newColumn += directions[i + 1];
                    newPosition = new ChessPosition(newRow, newColumn);

                    if (!board.isValidPosition(newPosition)) {
                        break;
                    }

                    newPiece = board.getPiece(newPosition);
                    if (newPiece != null && newPiece.getTeamColor() == teamColor) {
                        break;
                    }

                    validMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(validMove);
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int direction = teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int newRow = myPosition.getRow() + direction;
        int newColumn = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(newRow, newColumn);
        if (board.isValidPosition(newPosition) && board.getPiece(newPosition) == null) {
            if (newRow == 8 || newRow == 1) {
                moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        // Check if we are on the home row for double space moves
        if (myPosition.getRow() == 2 || myPosition.getRow() == 7) {
            if (board.getPiece(newPosition) == null) {
                newRow = myPosition.getRow() + 2 * direction;
                newColumn = myPosition.getColumn();
                newPosition = new ChessPosition(newRow, newColumn);
                if (board.isValidPosition(newPosition) && board.getPiece(newPosition) == null) {
                    ChessMove validMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(validMove);
                }
            }
        }

        int[] directions = new int[] {
                0, 1,  // diagonal-right
                0, -1  // diagonal-left
        };
        for (int i = 0; i < directions.length; i += 2) {
            newRow = myPosition.getRow() + direction;
            newColumn = myPosition.getColumn() + directions[i + 1];
            newPosition = new ChessPosition(newRow, newColumn);
            if (board.isValidPosition(newPosition)) {
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece != null && newPiece.getTeamColor() != teamColor) {
                    if (newRow == 8 || newRow == 1) {
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        return moves;
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
}
