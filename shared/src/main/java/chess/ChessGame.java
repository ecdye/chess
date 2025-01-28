package chess;

import java.util.Collection;
import java.util.ArrayList;

import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessPosition whiteKingPosition = new ChessPosition(1, 5);
    private ChessPosition blackKingPosition = new ChessPosition(8, 5);

    public ChessGame() {
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        ChessPosition savedKingPosition = null;
        if (piece == null) return null;
        if (piece.getPieceType() == PieceType.KING) {
            savedKingPosition = piece.getTeamColor() == TeamColor.WHITE ? whiteKingPosition : blackKingPosition;
        }
        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            // Save any captured piece
            ChessPiece capturedPiece = gameBoard.getPiece(move.getEndPosition());
            if (piece.getPieceType() == PieceType.KING) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    whiteKingPosition = move.getEndPosition();
                } else {
                    blackKingPosition = move.getEndPosition();
                }
            }

            // Mock the move
            gameBoard.addPiece(move.getEndPosition(), piece);
            gameBoard.removePiece(move.getStartPosition());

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            // Now undo it
            gameBoard.addPiece(startPosition, piece);
            if (capturedPiece != null) {
                gameBoard.addPiece(move.getEndPosition(), capturedPiece);
            } else {
                gameBoard.removePiece(move.getEndPosition());
            }
            if (savedKingPosition != null) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    whiteKingPosition = savedKingPosition;
                } else {
                    blackKingPosition = savedKingPosition;
                }
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        if (pieceToMove == null || pieceToMove.getTeamColor() != teamTurn) throw new InvalidMoveException();

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) throw new InvalidMoveException();

        if (pieceToMove.getPieceType() == PieceType.KING) {
            // Update kings position
            if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                whiteKingPosition = move.getEndPosition();
            } else {
                blackKingPosition = move.getEndPosition();
            }
        }

        if (move.getPromotionPiece() != null) {
            pieceToMove = new ChessPiece(teamTurn, move.getPromotionPiece());
        }

        gameBoard.addPiece(move.getEndPosition(), pieceToMove);
        gameBoard.removePiece(move.getStartPosition());
        setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        if (teamColor == TeamColor.WHITE) {
            kingPosition = whiteKingPosition;
        } else if (teamColor == TeamColor.BLACK) {
            kingPosition = blackKingPosition;
        }

        // Check if any opponent's piece can capture the king
        // TODO: Try and make this more better
        TeamColor opposingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == opposingTeam) {
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
        // Find and update king positions on the new board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == PieceType.KING) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        whiteKingPosition = position;
                    } else {
                        blackKingPosition = position;
                    }
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
