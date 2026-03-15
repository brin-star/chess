package server;

import dataaccess.*;
import handler.*;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;


    public Server() {
        try {
            userDAO = new MySqlUserDAO();
            authDAO = new MySqlAuthDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAOs: " + e.getMessage());
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Clear endpoint
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", clearHandler::clear);

        // Register endpoint
        RegisterService registerService = new RegisterService(authDAO, userDAO);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", registerHandler::register);

        // Login endpoint
        LoginService loginService = new LoginService(authDAO, userDAO);
        LoginHandler loginHandler = new LoginHandler(loginService);
        javalin.post("/session", loginHandler::login);

        // Logout endpoint
        LogoutService logoutService = new LogoutService(authDAO);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        javalin.delete("/session", logoutHandler::logout);

        // List games endpoint
        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(listGamesService);
        javalin.get("/game", listGamesHandler::listGames);

        // Create game endpoint
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        javalin.post("/game", createGameHandler::createGame);

        // Update game endpoint
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);
        javalin.put("/game", joinGameHandler::updateGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
