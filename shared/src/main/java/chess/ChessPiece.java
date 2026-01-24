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

        if (myPosition.getPieceType == PAWN) {
            pawnMoves(validMoves, board, myPosition);
            return validMoves;
        }
    }

    private void pawnMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition) {
        ChessPosition currentPosition = startPosition;

        // Determine the correct movement direction
        if (PieceType.getTeamColor() == WHITE) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn());
            moves.add(currentPosition);
        }

        if (PieceType.getTeamColor() == BLACK) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn());
            moves.add(currentPosition);
        }

        // Check if position is not off the board
        if (currentPosition.getRow() < 0 || currentPosition.getRow() > 7 || currentPosition.getColumn() < 0 || currentPosition.getColumn() > 7) {
            return;
        }

        // Check if there is a chess piece in front of the pawn
        if (currentPosition != null) {
            return;
        }

        // Check if the pawn made it to the other side of the board
        if (PieceType.getTeamColor() == WHITE && currentPosition.getRow() == 7) {
            currentPosition = PieceType.getPromotionPiece();
        }

        if (PieceType.getTeamColor() == BLACK && currentPosition.getRow() == 0) {
            currentPosition = PieceType.getPromotionPiece();
        }

        // Check if there are any pieces that can be captured diagonally
        if (PieceType.getTeamColor() == WHITE && ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1) != null) {
            currentPosition = new ChessPositionChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() + 1);
            moves.add(currentPosition);
        }

        if (PieceType.getTeamColor() == WHITE && ChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1) != null) {
            currentPosition = new ChessPositionChessPosition(currentPosition.getRow() + 1, currentPosition.getColumn() - 1);
            moves.add(currentPosition);
        }

        if (PieceType.getTeamColor() == BLACK && ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1) != null) {
            currentPosition = new ChessPositionChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() + 1);
            moves.add(currentPosition);
        }

        if (PieceType.getTeamColor() == BLACK && ChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1) != null) {
            currentPosition = new ChessPositionChessPosition(currentPosition.getRow() - 1, currentPosition.getColumn() - 1);
            moves.add(currentPosition);
        }

        // Check if this is the first move
        if (PieceType.getTeamColor() == WHITE && currentPosition.getRow() == 1) {
            currentPosition = new ChessPosition(currentPosition.getRow() + 2, currentPosition.getColumn());
            moves.add(currentPosition);
        }

        if (PieceType.getTeamColor() == BLACK && currentPosition.getRow() == 6) {
            currentPosition = new ChessPosition(currentPosition.getRow() - 2, currentPosition.getColumn());
            moves.add(currentPosition);
        }
    }
}
