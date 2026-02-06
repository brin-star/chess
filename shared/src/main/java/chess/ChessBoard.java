package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard copy = (ChessBoard) super.clone();
            copy.board = new ChessPiece[8][8];

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] != null) {
                        copy.board[i][j] = this.board[i][j].clone();
                    }
                }
            }

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int rowIndex = position.getRow() - 1;
        int colIndex = position.getColumn() - 1;

        if (piece != null) {
            board[rowIndex][colIndex] = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
        }
        else {
            board[rowIndex][colIndex] = null;
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int rowIndex = position.getRow() - 1;
        int colIndex = position.getColumn() - 1;
        if (board[rowIndex][colIndex] == null) {
            return null;
        }
        return board[rowIndex][colIndex];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

        // Add pieces to the starting positions

        // White pieces
        // Pawns
        for (int i = 1; i < 9; i++) {
            ChessPosition position = new ChessPosition(2, i);
            ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(position, piece);
        }
        // Rooks
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition position = new ChessPosition(1,1);
        addPiece(position, piece);
        position = new ChessPosition(1, 8);
        addPiece(position, piece);
        // Knights
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(1,2);
        addPiece(position, piece);
        position = new ChessPosition(1, 7);
        addPiece(position, piece);
        // Bishops
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(1,3);
        addPiece(position, piece);
        position = new ChessPosition(1, 6);
        addPiece(position, piece);
        // Queen
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(1,4);
        addPiece(position, piece);
        // King
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        position = new ChessPosition(1,5);
        addPiece(position, piece);

        // Black pieces
        // Pawns
        for (int i = 1; i < 9; i++) {
            position = new ChessPosition(7, i);
            piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(position, piece);
        }
        // Rooks
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        position = new ChessPosition(8,1);
        addPiece(position, piece);
        position = new ChessPosition(8, 8);
        addPiece(position, piece);
        // Knights
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(8,2);
        addPiece(position, piece);
        position = new ChessPosition(8, 7);
        addPiece(position, piece);
        // Bishops
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(8,3);
        addPiece(position, piece);
        position = new ChessPosition(8, 6);
        addPiece(position, piece);
        // Queen
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(8,4);
        addPiece(position, piece);
        // King
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        position = new ChessPosition(8,5);
        addPiece(position, piece);
    }
}
