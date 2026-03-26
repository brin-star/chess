package ui;

import ServerFacade.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreloginClient {

    private final ServerFacade serverFacade;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
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
                   register <username> <password> <email>  :create an account
                   login <username> <password>             :login to your account
                   quit                                    :exit the program
                   """;
        }
        else if (command.equals("register")) {
            if (tokens.size() != 4) {
                return """
                       Please include all and only required information to register:
                       register <username> <password> <email>
                       """;
            }

            try {
                var result = serverFacade.register(tokens.get(1), tokens.get(2), tokens.get(3));

                return "LOGIN_SUCCESS:" + result.authToken();
            }
            catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }
        else if (command.equals("login")) {
            if (tokens.size() != 3) {
                return """
                       Please include all and only required information to login:
                       login <username> <password>
                       """;
            }

            try {
                var result = serverFacade.login(tokens.get(1), tokens.get(2));

                return "LOGIN_SUCCESS:" + result.authToken();
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
                   register <username> <password> <email>  :create an account
                   login <username> <password>             :login to your account
                   quit                                    :exit the program
                   """;
        }
    }
}
