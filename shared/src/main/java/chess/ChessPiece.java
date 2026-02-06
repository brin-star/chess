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
public class ChessPiece implements Cloneable {
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

    @Override
    public ChessPiece clone() {
        try {
            ChessPiece copy = (ChessPiece) super.clone();

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
        else if (piece.getPieceType() == PieceType.ROOK) {
            rookMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        else if (piece.getPieceType() == PieceType.BISHOP) {
            bishopMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            bishopMoves(validMoves, board, myPosition, piece);
            rookMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        else if (piece.getPieceType() == PieceType.KING) {
            kingMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            knightMoves(validMoves, board, myPosition, piece);
            return validMoves;
        }
        return validMoves;
    }

    private void pawnMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        // Check if this is the first move (move two spaces)
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 2) {
            int forward = newRow + 2;
            int checkFirst = newRow + 1;

            ChessPosition firstPosition = new ChessPosition(checkFirst, newCol);
            if (board.getPiece(firstPosition) == null ) {
                moveforward(moves, board, startPosition, piece, forward, newCol, null);
            }
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 7) {
            int forward = newRow - 2;
            int checkFirst = newRow - 1;

            ChessPosition firstPosition = new ChessPosition(checkFirst, newCol);
            if (board.getPiece(firstPosition) == null ) {
                moveforward(moves, board, startPosition, piece, forward, newCol, null);
            }
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

    private void rookMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        // Check forward and backward movements
        int forward = newRow + 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(forward, newCol);

            if (forward < 1 || forward > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                ++forward;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, newCol, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        forward = newRow - 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(forward, newCol);

            if (forward < 1 || forward > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                --forward;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, newCol, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        // Check right movements
        int right = newCol + 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(newRow, right);

            if (right < 1 || right > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                ++right;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, newRow, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        // Check left movements
        int left = newCol - 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(newRow, left);

            if (left < 1 || left > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                --left;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, newRow, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }
    }

    private void bishopMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        // Check top right corner diagonal
        int forward = newRow + 1;
        int right = newCol + 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(forward, right);

            if (forward < 1 || forward > 8 || right < 1 || right > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                ++forward;
                ++right;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        // Check top left corner diagonal
        forward = newRow + 1;
        int left = newCol - 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(forward, left);

            if (forward < 1 || forward > 8 || left < 1 || left > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                ++forward;
                --left;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        // Check bottom right movements
        int backward = newRow - 1;
        right = newCol + 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(backward, right);

            if (backward < 1 || backward > 8 || right < 1 || right > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                --backward;
                ++right;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backward, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }

        // Check bottom left movements
        backward = newRow - 1;
        left = newCol - 1;
        while (true) {
            ChessPosition destinationPosition = new ChessPosition(backward, left);

            if (backward < 1 || backward > 8 || left < 1 || left > 8) {
                break;
            }

            if (board.getPiece(destinationPosition) == null) {
                moves.add(new ChessMove(startPosition, destinationPosition, null));
                --backward;
                --left;
            }
            else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backward, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
                break;
            }
            else {
                break;
            }
        }
    }

    private void kingMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        // Check forward and backward movements
        int forward = newRow + 1;

        if (forward >= 1 && forward <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forward, newCol);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forward, newCol, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, newCol, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        int backward = newRow - 1;
        if (backward >= 1 && backward <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backward, newCol);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backward, newCol, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backward, newCol, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Check right movements
        int right = newCol + 1;
        if (newRow >= 1 && newRow <= 8 && right >= 1 && right <= 8) {
            ChessPosition destinationPosition = new ChessPosition(newRow, right);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, newRow, right, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, newRow, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Check left movements
        int left = newCol - 1;
        if (newRow >= 1 && newRow <= 8 && left >= 1 && left <= 8) {
            ChessPosition destinationPosition = new ChessPosition(newRow, left);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, newRow, left, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, newRow, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Check top right corner diagonal
        if (forward >= 1 && forward <= 8 && right >= 1 && right <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forward, right);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forward, right, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }


        // Check top left corner diagonal
        if (forward >= 1 && forward <= 8 && left >= 1 && left <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forward, left);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forward, left, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forward, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Check bottom right movements
        if (backward >= 1 && backward <= 8 && right >= 1 && right <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backward, right);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backward, right, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backward, right, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Check bottom left movements
        if (backward >= 1 && backward <= 8 && left >= 1 && left <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backward, left);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backward, left, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backward, left, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }
    }

    private void knightMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        int forwardOne = newRow + 1;
        int forwardTwo = newRow + 2;
        int backwardOne = newRow - 1;
        int backwardTwo = newRow - 2;
        int rightOne = newCol + 1;
        int rightTwo = newCol + 2;
        int leftOne = newCol - 1;
        int leftTwo = newCol - 2;

        // Up two, right one
        if (forwardTwo >= 1 && forwardTwo <= 8 && rightOne >= 1 && rightOne <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forwardTwo, rightOne);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forwardTwo, rightOne, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forwardTwo, rightOne, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Up two, left one
        if (forwardTwo >= 1 && forwardTwo <= 8 && leftOne >= 1 && leftOne <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forwardTwo, leftOne);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forwardTwo, leftOne, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forwardTwo, leftOne, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Down two, right one
        if (backwardTwo >= 1 && backwardTwo <= 8 && rightOne >= 1 && rightOne <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backwardTwo, rightOne);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backwardTwo, rightOne, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backwardTwo, rightOne, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Down two, left one
        if (backwardTwo >= 1 && backwardTwo <= 8 && leftOne >= 1 && leftOne <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backwardTwo, leftOne);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backwardTwo, leftOne, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backwardTwo, leftOne, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Up one, right two
        if (forwardOne >= 1 && forwardOne <= 8 && rightTwo >= 1 && rightTwo <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forwardOne, rightTwo);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forwardOne, rightTwo, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forwardOne, rightTwo, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Up one, left two
        if (forwardOne >= 1 && forwardOne <= 8 && leftTwo >= 1 && leftTwo <= 8) {
            ChessPosition destinationPosition = new ChessPosition(forwardOne, leftTwo);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, forwardOne, leftTwo, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, forwardOne, leftTwo, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Down one, right two
        if (backwardOne >= 1 && backwardOne <= 8 && rightTwo >= 1 && rightTwo <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backwardOne, rightTwo);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backwardOne, rightTwo, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backwardOne, rightTwo, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }

        // Down one, left two
        if (backwardOne >= 1 && backwardOne <= 8 && leftTwo >= 1 && leftTwo <= 8) {
            ChessPosition destinationPosition = new ChessPosition(backwardOne, leftTwo);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, backwardOne, leftTwo, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, backwardOne, leftTwo, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }
    }

    private void moveforward(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece, int newRow, int newCol, ChessPiece.PieceType promotion) {
        // Check if position is not off the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            // Check if there is a chess piece in front of the piece
            ChessPosition destinationPosition = new ChessPosition(newRow, newCol);
            ChessPiece destinationPiece = board.getPiece(destinationPosition);
            // Check if it can be promoted
            if (piece.getPieceType() == PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8 && destinationPiece == null) {
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
                moves.add(validMove);
                validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
                moves.add(validMove);
            }
            else if (piece.getPieceType() == PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1 && destinationPiece == null) {
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
                if (piece.getPieceType() == PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) {
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
                else if (piece.getPieceType() == PieceType.PAWN && piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1) {
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
