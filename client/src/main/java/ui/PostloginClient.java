package ui;

import ServerFacade.ServerFacade;
import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PostloginClient {

    private final ServerFacade serverFacade;
    private String auth;
    private Collection<GameData> lastGamesList = new ArrayList<>();

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void setAuthToken(String authToken) {
        auth = authToken;
    }

    public String eval(String line) {
        if (line == null || line.isBlank()) {
            return "";
        }

        List<String> tokens = Arrays.stream(line.trim().split("\\s+")).collect(Collectors.toList());

        String command = tokens.get(0).toLowerCase();

        if (command.equals("help")) {
            return """
                   help                                    :show this message
                   logout                                  :logout of your account
                   list                                    :print all games
                   create <gameName>                       :create a game
                   play <gameNumber> <WHITE|BLACK>         :join a game
                   observe <gameNumber>                    :watch a game
                   quit                                    :exit the program
                   """;
        }
        else if (command.equals("logout")) {
            try {
                serverFacade.logout(auth);

                return "LOGOUT_SUCCESS";
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("list")) {
            try {
                var result = serverFacade.listGames(auth);
                lastGamesList = result.games();

                StringBuilder sb = new StringBuilder();

                int counter = 1;

                for (GameData game : lastGamesList) {
                    String whitePlayerName = game.whiteUsername();
                    String blackPlayerName = game.blackUsername();

                    if (game.whiteUsername() == null) {
                        whitePlayerName = "open";
                    }

                    if (game.blackUsername() == null) {
                        blackPlayerName = "open";
                    }

                    String gameOutput = counter + ". GAME NAME: " + game.gameName() + ", WHITE PLAYER NAME: " + whitePlayerName
                            + ", BLACK PLAYER NAME: " + blackPlayerName + "\n";

                    sb.append(gameOutput);
                    counter++;
                }

                if (sb.isEmpty()) {
                    return "No games found.";
                }
                else {
                    return sb.toString();
                }
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("create")) {
            if (tokens.size() != 2) {
                return """
                       Please include all and only required information to create:
                       create <gameName>
                       """;
            }

            try {
                serverFacade.createGame(auth, tokens.get(1));

                return "Game created successfully!";
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("play")) {
            if (tokens.size() != 3) {
                return """
                       Please include all and only required information to play:
                       play <gameNumber> <WHITE|BLACK>
                       """;
            }

            try {
                int gameNumber = Integer.parseInt(tokens.get(1));
                int gameIndex = gameNumber - 1;

                if (lastGamesList == null || lastGamesList.isEmpty()) {
                    return "Please check available games first by running 'list.'";
                }

                GameData game = new ArrayList<>(lastGamesList).get(gameIndex);

                int gameID = game.gameID();
                ChessGame.TeamColor playerColor = ChessGame.TeamColor.valueOf(tokens.get(2));

                serverFacade.joinGame(auth, gameID, playerColor);
                return "Game joined successfully!";
            }
            catch (NumberFormatException e) {
                return "Game number must be a number.";
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("observe")) {
            if (tokens.size() != 2) {
                return """
                       Please include all and only required information to observe:
                       observe <gameNumber>
                       """;
            }

            try {
                int gameNumber = Integer.parseInt(tokens.get(1));
                int gameIndex = gameNumber - 1;

                if (lastGamesList == null || lastGamesList.isEmpty()) {
                    return "Please check available games first by running 'list.'";
                }

                GameData game = new ArrayList<>(lastGamesList).get(gameIndex);

                int gameID = game.gameID();

                return "Now observing game: " + game.gameName();
            }
            catch (NumberFormatException e) {
                return "Game number must be a number.";
            }
            catch (IndexOutOfBoundsException e) {
                return "Game number out of range.";
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("quit")) {
            return "quit";
        }
        else {
            return """
                   Please enter one of these commands:
                   
                   help                                    :show this message
                   logout                                  :logout of your account
                   list                                    :print all games
                   create <gameName>                       :create a game
                   play <gameNumber> <WHITE|BLACK>         :join a game
                   observe <gameNumber>                    :watch a game
                   quit                                    :exit the program
                   """;
        }
    }

    public void drawBoard(ChessGame.TeamColor boardPerspective) {
        StringBuilder sb = new StringBuilder();
        List<Integer> row;
        List<String> column;
        List<String> chessPieces;
        int startRow;
        int pieceColor;
        int enemyColor;
        int background;
        int textColor;
        String text;

        if (boardPerspective == ChessGame.TeamColor.WHITE) {
            row = List.of(8, 7, 6, 5, 4, 3, 2, 1);
            column = List.of("a", "b", "c", "d", "e", "f", "g", "h");
            chessPieces = List.of("R", "N", "B", "Q", "K", "B", "N", "R");
            startRow = 8;
            pieceColor = 37;
            enemyColor = 30;
        }
        else {
            row = List.of(1, 2, 3, 4, 5, 6, 7, 8);
            column = List.of("h", "g", "f", "e", "d", "c", "b", "a");
            chessPieces = List.of("R", "N", "B", "K", "Q", "B", "N", "R");
            startRow = 1;
            pieceColor = 31;
            enemyColor = 30;
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
                // Determine background color
                if ((i + k) % 2 != 0) {
                    background = 47;
                }
                else {
                    background = 105;
                }

                // Determine the piece and piece color
                if (i == 0) {
                    text = chessPieces.get(k);
                    textColor = pieceColor;
                }
                else if (i == 1) {
                    text = "P";
                    textColor = pieceColor;
                }
                else if (i == 7) {
                    text = chessPieces.get(k);
                    textColor = enemyColor;
                }
                else if (i == 6) {
                    text = "P";
                    textColor = enemyColor;
                }
                else {
                    text = " ";
                    textColor = 0;
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
}
