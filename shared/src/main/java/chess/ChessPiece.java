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
        int forward;
        int startRow;
        int promotionRow;
        ChessGame.TeamColor oppColor;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forward = 1;
            startRow = 2;
            promotionRow = 8;
            oppColor = ChessGame.TeamColor.BLACK;
        }
        else {
            forward = -1;
            startRow = 7;
            promotionRow = 1;
            oppColor = ChessGame.TeamColor.WHITE;
        }

        // Check if this is the first move (move two spaces)
        if (startPosition.getRow() == startRow) {
            int move = newRow + (forward * 2);
            int checkFirst = newRow + forward;

            ChessPosition firstPosition = new ChessPosition(checkFirst, newCol);
            if (board.getPiece(firstPosition) == null ) {
                moveforward(moves, board, startPosition, piece, move, newCol, null);
            }
        }

        // Move one space forward
        moveforward(moves, board, startPosition, piece, newRow + forward, newCol, null);

        // Check if there are any pieces that can be captured diagonally
        capture(moves, board, startPosition, newRow + forward, newCol + forward, piece, oppColor, null);
        capture(moves, board, startPosition, newRow + forward, newCol - forward, piece, oppColor, null);
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

        int[][] kingPositions = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        checkPositions(kingPositions, newRow, newCol, board, startPosition, piece, moves);
    }

    private void knightMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, ChessPiece piece) {
        int newRow = startPosition.getRow();
        int newCol = startPosition.getColumn();

        int[][] knightPositions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        checkPositions(knightPositions, newRow, newCol, board, startPosition, piece, moves);
    }

    public void checkPositions(int[][] positions, int newRow, int newCol,
                               ChessBoard board, ChessPosition startPosition,
                               ChessPiece piece, Collection<ChessMove> moves) {
        for (int[] position : positions) {
            int destinationRow = newRow + position[0];
            int destinationCol = newCol + position[1];

            if (destinationRow < 1 || destinationRow > 8 || destinationCol < 1 || destinationCol > 8) {
                continue;
            }

            ChessPosition destinationPosition = new ChessPosition(destinationRow, destinationCol);

            if (board.getPiece(destinationPosition) == null) {
                moveforward(moves, board, startPosition, piece, destinationRow, destinationCol, null);
            } else if (board.getPiece(destinationPosition).getTeamColor() != piece.getTeamColor()) {
                capture(moves, board, startPosition, destinationRow, destinationCol, piece, board.getPiece(destinationPosition).getTeamColor(), null);
            }
        }
    }

    private void moveforward(Collection<ChessMove> moves, ChessBoard board,
                             ChessPosition startPosition, ChessPiece piece,
                             int newRow, int newCol, ChessPiece.PieceType promotion) {
        // Check if position is not off the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            // Check if there is a chess piece in front of the piece
            ChessPosition destinationPosition = new ChessPosition(newRow, newCol);
            ChessPiece destinationPiece = board.getPiece(destinationPosition);
            // Check if it can be promoted
            if (piece.getPieceType() == PieceType.PAWN
                    && ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8)
                            || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1))
                    && destinationPiece == null) {
                promote(moves, startPosition, destinationPosition);
            }
            // Can't be promoted
            else if (destinationPiece == null) {
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, promotion);
                moves.add(validMove);
            }
        }
    }

    private void promote(Collection<ChessMove> moves, ChessPosition startPosition, ChessPosition destinationPosition) {
        ChessMove validMove = new ChessMove(startPosition, destinationPosition, PieceType.QUEEN);
        moves.add(validMove);
        validMove = new ChessMove(startPosition, destinationPosition, PieceType.BISHOP);
        moves.add(validMove);
        validMove = new ChessMove(startPosition, destinationPosition, PieceType.ROOK);
        moves.add(validMove);
        validMove = new ChessMove(startPosition, destinationPosition, PieceType.KNIGHT);
        moves.add(validMove);
    }

    private void capture(Collection<ChessMove> moves, ChessBoard board,
                         ChessPosition startPosition, int newRow, int newCol,
                         ChessPiece piece, ChessGame.TeamColor enemyColor,
                         ChessPiece.PieceType promotion) {
        // Check if position is not off the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            // Check if there is a chess piece in front of the piece
            ChessPosition destinationPosition = new ChessPosition(newRow, newCol);
            ChessPiece destinationPiece = board.getPiece(destinationPosition);
            if (destinationPiece != null && destinationPiece.getTeamColor() == enemyColor) {
                if (piece.getPieceType() == PieceType.PAWN
                        && ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8)
                        || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1))) {
                    promote(moves, startPosition, destinationPosition);
                    return;
                }
                ChessMove validMove = new ChessMove(startPosition, destinationPosition, promotion);
                moves.add(validMove);
            }
        }
    }
}
