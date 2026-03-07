package server;

import dataaccess.*;
import handler.ClearHandler;
import handler.LoginHandler;
import handler.RegisterHandler;
import io.javalin.*;
import service.ClearService;
import service.LoginService;
import service.RegisterService;

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

        // Register endpoint
        RegisterService registerService = new RegisterService(authDAO, userDAO);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        javalin.post("/user", registerHandler::register);

        // Login endpoint
        LoginService loginService = new LoginService(authDAO, userDAO);
        LoginHandler loginHandler = new LoginHandler(loginService);
        javalin.post("/session", loginHandler::login);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
