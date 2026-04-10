package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class BoardDrawer {

    public static void draw(ChessGame currentGame, ChessGame.TeamColor playerColor) {
        drawWithHighlights(currentGame, playerColor, null, null);
    }

    public static void drawWithHighlights(ChessGame currentGame, ChessGame.TeamColor playerColor, Collection<ChessMove> validMoves, ChessPosition startPosition) {
        StringBuilder sb = new StringBuilder();
        List<Integer> row;
        List<String> column;
        int background;
        int textColor;
        String text;

        if (playerColor == ChessGame.TeamColor.WHITE) {
            row = List.of(8, 7, 6, 5, 4, 3, 2, 1);
            column = List.of("a", "b", "c", "d", "e", "f", "g", "h");
        }
        else {
            row = List.of(1, 2, 3, 4, 5, 6, 7, 8);
            column = List.of("h", "g", "f", "e", "d", "c", "b", "a");
        }

        // Top boarder row
        sb.append("   ");
        for (int i = 0; i < column.size(); i++) {
            sb.append(" " + column.get(i) + " ");
        }
        sb.append("   ");
        sb.append("\n");

        // Rows of Chess Board
        for (int i = 0; i < row.size(); i++) {
            sb.append(" " + row.get(i) + " ");

            for (int k = 0; k < column.size(); k++) {
                int col = (playerColor == ChessGame.TeamColor.BLACK) ? (8 - k) : (k + 1);
                ChessPosition position = new ChessPosition(row.get(i), col);
                ChessPiece piece = currentGame.getBoard().getPiece(position);

                // Determine background color
                if (validMoves != null) {
                    if (startPosition.equals(position)) {
                        background = 226;
                    }
                    else if (validMoves.stream().anyMatch(move -> move.getEndPosition().equals(position))) {
                        background = 46;
                    }
                    else {
                        if ((i + k) % 2 != 0) {
                            background = 47;
                        }
                        else {
                            background = 105;
                        }
                    }
                }
                else {
                    if ((i + k) % 2 != 0) {
                        background = 47;
                    }
                    else {
                        background = 105;
                    }
                }

                // Determine the piece and piece color
                if (piece == null) {
                    text = " ";
                    textColor = 31;
                }
                else {
                    text = getPieceLetter(piece.getPieceType());
                    textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 30 : 31;
                }

                sb.append("\u001b[" + textColor + ";" + background + ";1m " + text + " \u001b[0m");
            }

            sb.append(" " + row.get(i) + " ");
            sb.append("\n");
        }

        // bottom boarder row
        sb.append("   ");
        for (int i = 0; i < column.size(); i++) {
            sb.append(" " + column.get(i) + " ");
        }
        sb.append("   ");
        sb.append("\n");

        System.out.print(sb);
    }

    public static String getPieceLetter(ChessPiece.PieceType pieceType) {
        String letter = "";

        if (pieceType == ChessPiece.PieceType.KING) {
            letter = "K";
        }
        else if (pieceType == ChessPiece.PieceType.QUEEN) {
            letter = "Q";
        }
        else if (pieceType == ChessPiece.PieceType.KNIGHT) {
            letter = "N";
        }
        else if (pieceType == ChessPiece.PieceType.ROOK) {
            letter = "R";
        }
        else if (pieceType == ChessPiece.PieceType.BISHOP) {
            letter = "B";
        }
        else if (pieceType == ChessPiece.PieceType.PAWN) {
            letter = "P";
        }

        return letter;
    }
}
