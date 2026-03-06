package server;

import dataaccess.*;
import handler.ClearHandler;
import io.javalin.*;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;


    public Server() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Clear endpoint
        ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        javalin.delete("/db", clearHandler::clear);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
