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
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    private ChessGame.TeamColor color;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
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
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPiece piece = board.getPiece(myPosition);

        if (piece.getPieceType() == PieceType.PAWN) {
            pawnMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        return validMoves;
    }

    private void pawnMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        ChessPosition currentPosition = startPosition;

        // Check if this is the first move (move two spaces)
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentPosition.getRow() == 1) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn());
            ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
            moves.add(validMove);
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentPosition.getRow() == 6) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn());
            ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
            moves.add(validMove);
        }

        // Move one space forward
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece in front of the pawn
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece == null) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece in front of the pawn
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece == null) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        // Check if there are any pieces that can be captured diagonally
        // White pawns
        ChessPosition diagonalPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && board.getPiece(diagonalPosition) != null) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece is the opposing team
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        diagonalPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && board.getPiece(diagonalPosition) != null) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece is the opposing team
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        // Black pawns
        diagonalPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && board.getPiece(diagonalPosition) != null) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece is the opposing team
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        diagonalPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && board.getPiece(diagonalPosition) != null) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
            // Check if position is not off the board
            if (currentPosition.getRow() >= 0 && currentPosition.getRow() <= 7 && currentPosition.getColumn() >= 0 && currentPosition.getColumn() <= 7) {
                // Check if there is a chess piece is the opposing team
                ChessPiece destinationPiece = board.getPiece(currentPosition);
                if (destinationPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    ChessMove validMove = new ChessMove(startPosition, currentPosition, null);
                    moves.add(validMove);
                }
            }
        }

        // Check if the pawn made it to the other side of the board and promote it
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && currentPosition.getRow() == 7) {
            ChessMove validMove = new ChessMove(startPosition, currentPosition, PieceType.QUEEN);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.BISHOP);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.ROOK);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.KNIGHT);
            moves.add(validMove);
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && currentPosition.getRow() == 0) {
            ChessMove validMove = new ChessMove(startPosition, currentPosition, PieceType.QUEEN);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.BISHOP);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.ROOK);
            moves.add(validMove);
            validMove = new ChessMove(startPosition, currentPosition, PieceType.KNIGHT);
            moves.add(validMove);
        }
    }
}
