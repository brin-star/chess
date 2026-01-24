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
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        // Check if this is the first move (move two spaces)
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 1) {
            int forward = newRow + 2;
            moveforward(moves, board, startPosition, piece, forward, newCol, null);
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 6) {
            int forward = newRow - 2;
            moveforward(moves, board, startPosition, piece, forward, newCol, null);
        }

        // Move one space forward
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            int forward = newRow + 1;
            moveforward(moves, board, startPosition, piece, forward, newCol, null);
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            int forward = newRow - 1;
            moveforward(moves, board, startPosition, piece, forward, newCol, null);
        }

        // Check if there are any pieces that can be captured diagonally
        // White pawns
        int diagonalRow = newRow + 1;
        int diagonalCol = newCol + 1;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            capture(moves, board, startPosition, diagonalRow, diagonalCol, piece, ChessGame.TeamColor.BLACK, null);
        }

        diagonalCol = newCol - 1;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            capture(moves, board, startPosition, diagonalRow, diagonalCol, piece, ChessGame.TeamColor.BLACK, null);
        }

        // Black pawns
        diagonalRow = newRow - 1;
        diagonalCol = newCol + 1;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            capture(moves, board, startPosition, diagonalRow, diagonalCol, piece, ChessGame.TeamColor.WHITE, null);
        }

        diagonalCol = newCol - 1;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            capture(moves, board, startPosition, diagonalRow, diagonalCol, piece, ChessGame.TeamColor.WHITE, null);
        }
    }

    private void moveforward(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece, int newRow, int newCol, ChessPiece.PieceType promotion) {
        // Check if position is not off the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            // Check if there is a chess piece in front of the piece
            ChessPosition destinationPosition = new ChessPosition(newRow, newCol);
            ChessPiece destinationPiece = board.getPiece(destinationPosition);
            // Check if it can be promoted
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8 && destinationPiece == null) {
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
                moves.add(validMove);
            }
            else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1 && destinationPiece == null) {
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
                moves.add(validMove);
            }
            // Can't be promoted
            else if (destinationPiece == null) {
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, promotion);
                moves.add(validMove);
            }
        }
    }

    private void capture(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, int newRow, int newCol, ChessPiece piece, ChessGame.TeamColor enemyColor, ChessPiece.PieceType promotion) {
        // Check if position is not off the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            // Check if there is a chess piece in front of the piece
            ChessPosition destinationPosition = new ChessPosition(newRow, newCol);
            ChessPiece destinationPiece = board.getPiece(destinationPosition);
            if (destinationPiece != null && destinationPiece.getTeamColor() == enemyColor) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) {
                    ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
                    moves.add(validMove);
                    return;
                }
                else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1) {
                    ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
                    moves.add(validMove);
                    validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
                    moves.add(validMove);
                    return;
                }
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, promotion);
                moves.add(validMove);
            }
        }
    }
}
