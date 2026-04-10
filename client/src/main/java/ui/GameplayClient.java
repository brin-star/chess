package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import serverfacade.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Scanner;

public class GameplayClient implements ServerMessageObserver {
    ServerFacade serverFacade;
    String authToken;
    int gameID;
    ChessGame.TeamColor playerColor;
    ChessGame currentGame;
    boolean inGame = true;
    private final Scanner scanner = new Scanner(System.in);

    public GameplayClient(ServerFacade serverFacade, String authToken, int gameID, ChessGame.TeamColor playerColor) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    @Override
    public void receiveMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame((LoadGameMessage) message);
            case ERROR -> handleError((ErrorMessage) message);
            case NOTIFICATION -> handleNotification((NotificationMessage) message);
        }
    }

    private void handleLoadGame(LoadGameMessage message) {
        this.currentGame = message.getGame().game();
        drawBoard(currentGame, playerColor);
    }

    private void handleError(ErrorMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED +
                message.getErrorMessage() +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    private void handleNotification(NotificationMessage message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW +
                message.getMessage() +
                EscapeSequences.RESET_TEXT_COLOR);
    }

    public void run() {
        while(inGame) {
            System.out.print("[IN_GAME] >>> ");
            String line = scanner.nextLine();
            String result = eval(line);
            if (result.equals("leave")) {
                inGame = false;
            }
        }
    }

    public String eval(String input) {
        return switch (input.toLowerCase()) {
            case "help" -> helpText();
            case "redraw" -> { drawBoard(currentGame, playerColor); yield ""; }
            case "move" -> { promptAndMakeMove(); yield ""; }
            case "resign" -> { promptAndResign(); yield ""; }
            case "highlight" -> { promptAndHighlight(); yield ""; }
            case "leave" -> {
                try { serverFacade.leaveGame(authToken, gameID); }
                catch (IOException e) { yield "Error: " + e.getMessage(); }
                yield "leave";
            }
            default -> "Unknown command. Type 'help'.";
        };
    }

    private String helpText() {
        return """
                   Please enter one of these commands:
                   
                   help               :show this message
                   redraw             :show the board
                   move               :move a piece
                   resign             :resign from a game
                   highlight          :highlight possible moves
                   leave              :leave the game
                   """;
    }

    private void promptAndMakeMove() {
        System.out.print("Enter start position (e.g. e2): ");
        String start = scanner.nextLine();
        System.out.print("Enter end position (e.g. e4): ");
        String end = scanner.nextLine();

        char colNum = start.charAt(0);
        char rowLetter = start.charAt(1);
        int row = rowLetter - '0';
        int col = colNum - 'a' + 1;
        ChessPosition startPosition = new ChessPosition(row, col);

        colNum = end.charAt(0);
        rowLetter = end.charAt(1);
        row = rowLetter - '0';
        col = colNum - 'a' + 1;
        ChessPosition endPosition = new ChessPosition(row, col);

        ChessPiece.PieceType promotionPiece = null;
        ChessPiece piece = currentGame.getBoard().getPiece(startPosition);

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8)
                    || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
                System.out.print("Promote to (QUEEN, ROOK, BISHOP, KNIGHT): ");
                String choice = scanner.nextLine();
                promotionPiece = ChessPiece.PieceType.valueOf(choice.toUpperCase());
            }
        }

        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);

        try {
            serverFacade.makeMove(authToken, gameID, move);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void promptAndResign() {
        System.out.print("Are you sure you want to resign? [yes/no]\n");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("yes")) {
            try {
                serverFacade.resignGame(authToken, gameID);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void promptAndHighlight() {
        System.out.print("Enter square to highlight (e.g. b3): ");
        String square = scanner.nextLine();

        char colNum = square.charAt(0);
        char rowLetter = square.charAt(1);
        int row = rowLetter - '0';
        int col = colNum - 'a' + 1;
        ChessPosition position = new ChessPosition(row, col);
        var validMoves = currentGame.validMoves(position);
        drawBoard(currentGame, playerColor, validMoves, position);
    }
}
