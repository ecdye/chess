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
    private boolean whiteKingMoved = false;
    private ChessPosition blackKingPosition = new ChessPosition(8, 5);
    private boolean blackKingMoved = false;
    private boolean whiteRookLMoved = false;
    private boolean whiteRookRMoved = false;
    private boolean blackRookLMoved = false;
    private boolean blackRookRMoved = false;
    private boolean passantTurn = false;

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
        if (piece == null) {
            return null;
        }

        TeamColor color = piece.getTeamColor();
        ChessPosition savedKingPosition = null;
        if (piece.getPieceType() == PieceType.KING) {
            savedKingPosition = color == TeamColor.WHITE ? whiteKingPosition : blackKingPosition;
        }
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> moves = piece.pieceMoves(gameBoard, startPosition);

        for (ChessMove move : moves) {
            // Save any captured piece
            ChessPiece capturedPiece = gameBoard.getPiece(move.getEndPosition());
            if (piece.getPieceType() == PieceType.KING) {
                if (color == TeamColor.WHITE) {
                    whiteKingPosition = move.getEndPosition();
                } else {
                    blackKingPosition = move.getEndPosition();
                }
            }

            // Mock the move
            gameBoard.addPiece(move.getEndPosition(), piece);
            gameBoard.removePiece(move.getStartPosition());

            if (!isInCheck(color)) {
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
                if (color == TeamColor.WHITE) {
                    whiteKingPosition = savedKingPosition;
                } else {
                    blackKingPosition = savedKingPosition;
                }
            }
        }

        if (piece.getPieceType() == PieceType.KING) {
            validCastleMoves(validMoves, startPosition);
        }
        if (piece.getPieceType() == PieceType.PAWN && passantTurn) {
            validEnPassantMoves(validMoves, startPosition);
        }

        return validMoves;
    }

    private void validCastleMoves(Collection<ChessMove> validMoves, ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        boolean isWhite = piece.getTeamColor() == TeamColor.WHITE;
        boolean kingMoved = isWhite ? whiteKingMoved : blackKingMoved;
        boolean rookLMoved = isWhite ? whiteRookLMoved : blackRookLMoved;
        boolean rookRMoved = isWhite ? whiteRookRMoved : blackRookRMoved;
        int row = isWhite ? 1 : 8;
        ChessPosition kingPos = isWhite ? whiteKingPosition : blackKingPosition;

        if (!kingMoved) {
            if (!rookLMoved) {
                ChessPosition midKing = new ChessPosition(row, 4);
                ChessPosition endPos = new ChessPosition(row, 3);
                ChessPosition midRook = new ChessPosition(row, 2);
                if (validMoves.contains(new ChessMove(kingPos, midKing, null)) &&
                gameBoard.getPiece(midKing) == null &&
                gameBoard.getPiece(endPos) == null &&
                gameBoard.getPiece(midRook) == null) {
                    validMoves.add(new ChessMove(startPosition, endPos, null));
                }
            }
            if (!rookRMoved) {
                ChessPosition midKing = new ChessPosition(row, 6);
                ChessPosition endPos = new ChessPosition(row, 7);
                if (validMoves.contains(new ChessMove(kingPos, midKing, null)) &&
                gameBoard.getPiece(midKing) == null &&
                gameBoard.getPiece(endPos) == null) {
                    validMoves.add(new ChessMove(startPosition, endPos, null));
                }
            }
        }
    }

    private void validEnPassantMoves(Collection<ChessMove> validMoves, ChessPosition startPosition) {
        ChessPosition leftSide = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
        ChessPosition rightSide = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);
        ChessPiece piece = gameBoard.getPiece(startPosition);
        ChessPiece left = gameBoard.getPiece(leftSide);
        ChessPiece right = gameBoard.getPiece(rightSide);
        int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;

        if (left != null && left.getPieceType() == PieceType.PAWN &&
            left.getTeamColor() != teamTurn) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + direction,
                    startPosition.getColumn() - 1), null));
        }
        if (right != null && right.getPieceType() == PieceType.PAWN &&
            right.getTeamColor() != teamTurn) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow() + direction,
                    startPosition.getColumn() + 1), null));
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        if (pieceToMove == null || pieceToMove.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        switch (pieceToMove.getPieceType()) {
            case PieceType.KING:
                makeCastleMove(move);

                // Update kings position
                if (teamTurn == TeamColor.WHITE) {
                    whiteKingPosition = move.getEndPosition();
                    whiteKingMoved = true;
                } else {
                    blackKingPosition = move.getEndPosition();
                    blackKingMoved = true;
                }
            case PieceType.ROOK:
                int baseRow = (teamTurn == TeamColor.WHITE) ? 1 : 8;
                if (move.getStartPosition().equals(new ChessPosition(baseRow, 1))) {
                    if (teamTurn == TeamColor.WHITE) {
                        whiteRookLMoved = true;
                    } else {
                        blackRookLMoved = true;
                    }
                } else if (move.getStartPosition().equals(new ChessPosition(baseRow, 8))) {
                    if (teamTurn == TeamColor.WHITE) {
                        whiteRookRMoved = true;
                    } else {
                        blackRookRMoved = true;
                    }
                }
            case PieceType.PAWN:
                passantTurn = false;
                baseRow = (teamTurn == TeamColor.WHITE) ? 2 : 7;
                int direction = (teamTurn == TeamColor.WHITE) ? 2 : -2;
                if (move.getStartPosition().getRow() == baseRow &&
                    move.getEndPosition().getRow() == baseRow + direction) {
                        passantTurn = true;
                }
                if (gameBoard.getPiece(move.getEndPosition()) == null) {
                    ChessPosition side = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                    ChessPiece sidePiece = gameBoard.getPiece(side);
                    if (sidePiece != null && sidePiece.getPieceType() == PieceType.PAWN) {
                        gameBoard.removePiece(side);
                    }
                }
            default:
                if (move.getPromotionPiece() != null) {
                    pieceToMove = new ChessPiece(teamTurn, move.getPromotionPiece());
                }

                gameBoard.addPiece(move.getEndPosition(), pieceToMove);
                gameBoard.removePiece(move.getStartPosition());
                break;
        }

        setTeamTurn(teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    private void makeCastleMove(ChessMove move) {
        if ((teamTurn == TeamColor.WHITE && whiteKingMoved) ||
            (teamTurn == TeamColor.BLACK && blackKingMoved)) return;

        int row = teamTurn == TeamColor.WHITE ? 1 : 8;
        ChessPosition kingPosition = teamTurn == TeamColor.WHITE ? whiteKingPosition : blackKingPosition;
        ChessPosition rookStart;
        ChessPosition rookEnd;

        if (move.equals(new ChessMove(kingPosition, new ChessPosition(row, 3), null))) {
            rookStart = new ChessPosition(row, 1);
            rookEnd = new ChessPosition(row, 4);
        } else if (move.equals(new ChessMove(kingPosition, new ChessPosition(row, 7), null))) {
            rookStart = new ChessPosition(row, 8);
            rookEnd = new ChessPosition(row, 6);
        } else {
            return;
        }

        ChessPiece rook = gameBoard.getPiece(rookStart);
        gameBoard.removePiece(rookStart);
        gameBoard.addPiece(rookEnd, rook);
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
        TeamColor opposingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(position);
                if (piece == null || piece.getTeamColor() != opposingTeam) {
                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(gameBoard, position);
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
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
                    if (!validMoves(position).isEmpty()) {
                        return false;
                    }
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
        if (!isInCheckmate(teamColor) || isInCheck(teamColor)) {
            return false;
        }
        return true;
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
        resetHasBeenMoved();
    }

    private void resetHasBeenMoved() {
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookLMoved = false;
        whiteRookRMoved = false;
        blackRookLMoved = false;
        blackRookRMoved = false;

        ChessPiece whiteKing = gameBoard.getPiece(new ChessPosition(1, 5));
        ChessPiece blackKing = gameBoard.getPiece(new ChessPosition(8, 5));
        if (whiteKing == null || whiteKing.getPieceType() != PieceType.KING ||
            whiteKing.getTeamColor() != TeamColor.WHITE) {
                whiteKingMoved = true;
        }
        if (blackKing == null || blackKing.getPieceType() != PieceType.KING ||
            blackKing.getTeamColor() != TeamColor.BLACK) {
                blackKingMoved = true;
        }

        ChessPiece leftWhiteRook = gameBoard.getPiece(new ChessPosition(1, 1));
        ChessPiece rightWhiteRook = gameBoard.getPiece(new ChessPosition(1, 8));
        if (leftWhiteRook == null || leftWhiteRook.getPieceType() != PieceType.ROOK) {
            whiteRookLMoved = true;
        }
        if (rightWhiteRook == null || rightWhiteRook.getPieceType() != PieceType.ROOK) {
            whiteRookRMoved = true;
        }

        ChessPiece leftBlackRook = gameBoard.getPiece(new ChessPosition(8, 1));
        ChessPiece rightBlackRook = gameBoard.getPiece(new ChessPosition(8, 8));
        if (leftBlackRook == null || leftBlackRook.getPieceType() != PieceType.ROOK) {
            blackRookLMoved = true;
        }
        if (rightBlackRook == null || rightBlackRook.getPieceType() != PieceType.ROOK) {
            blackRookRMoved = true;
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
