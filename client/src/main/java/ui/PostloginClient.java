package ui;

import ServerFacade.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PostloginClient {

    private final ServerFacade serverFacade;
    private String auth;

    public PostloginClient(ServerFacade serverFacade, String auth) {
        this.serverFacade = serverFacade;
        this.auth = auth;
    }

    public void setAuthToken(String authToken) {
        auth = authToken;
    }

    public String eval(String line) {
        List<String> tokens = Arrays.stream(line.trim().split("\\s+")).collect(Collectors.toList());

        String command = tokens.get(0).toLowerCase();

        if (line == null || line.isBlank()) {
            return "";
        }

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
                String result = String.valueOf(serverFacade.listGames(auth));

                return result;
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
            return "Coming soon!";
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
