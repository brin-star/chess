package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    private ChessBoard board;
    private TeamColor turn = TeamColor.WHITE;
    private boolean gameOver = false;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
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
        ChessPiece piece = board.getPiece(startPosition);

        // Check that there is a piece at that position
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessBoard boardCopy;
        ChessBoard boardOriginal = board;

        // Check every move in the collection of possible moves
        for (ChessMove move : possibleMoves) {
            ChessPosition endPosition = move.getEndPosition();

            // Move piece on the copy board
            boardCopy = board.clone();
            boardCopy.addPiece(endPosition, piece);
            boardCopy.addPiece(startPosition, null);
            TeamColor pieceColor = piece.getTeamColor();

            // Temporarily change board to board copy so isInCheck() works
            board = boardCopy;

            // Make sure it doesn't leave the king exposed
            if (isInCheck(pieceColor) == false) {
                validMoves.add(move);
            }
            board = boardOriginal;
        } 

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);

        // Check if there is a piece selected and that it is the right color
        if (piece == null) {
            throw new InvalidMoveException("There is no piece selected");
        }

        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Wrong team selected");
        }

        // Check if move is valid
        Collection<ChessMove> possibleMoves = validMoves(startPosition);
        boolean hasMove = possibleMoves.contains(move);

        if (hasMove == false) {
            throw new InvalidMoveException("Not a valid move");
        }

        // Move piece and change turns
        ChessPosition endPosition = move.getEndPosition();
        board.addPiece(endPosition, piece);
        board.addPiece(startPosition, null);

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece (turn, move.getPromotionPiece());
            board.addPiece(endPosition,piece);
        }

        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(0, 0);
        ChessPosition currentPosition;
        ChessPiece piece;
        TeamColor oppColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Find king position
        positionLoop:
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPosition = currentPosition;
                        break positionLoop;
                    }
                }
            }
        }

        // Check if a piece can capture the king
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() == oppColor) {
                    if (kingThreatened(piece, currentPosition, kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the king is threatened
     *
     * @param piece
     * @param currentPosition
     * @param kingPosition
     * @return True if the king is threatened
     */
    public boolean kingThreatened(ChessPiece piece, ChessPosition currentPosition, ChessPosition kingPosition) {
        Collection<ChessMove> moves = piece.pieceMoves(board, currentPosition);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
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
        return checkMoves(teamColor, false);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return checkMoves(teamColor, true);
    }

    /**
     * This function does the checking for
     * isInStalemate and isInCheckmate
     *
     * @param teamColor
     * @param check
     * @return True if checkmate or stalemate
     */
    public boolean checkMoves(TeamColor teamColor, boolean check) {
        // Check if the team is in check
        if (isInCheck(teamColor) == check) {
            return false;
        }

        ChessPosition currentPosition;
        ChessPiece piece;

        // Check if the team has any valid moves (and therefore has an escape)
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                currentPosition = new ChessPosition(row, col);
                piece = board.getPiece(currentPosition);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(currentPosition).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
