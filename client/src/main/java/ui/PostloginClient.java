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
                       Please include all and only required information to login:
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
                       Please include all and only required information to login:
                       play <gameNumber> <WHITE|BLACK>
                       """;
            }

            try {
                int gameNumber = Integer.parseInt(tokens.get(1));
                int gameIndex = gameNumber - 1;

                if (lastGamesList.isEmpty() || lastGamesList == null) {
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
            return "Coming soon!";
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
}
