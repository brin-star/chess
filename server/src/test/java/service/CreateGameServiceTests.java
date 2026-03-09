package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.CreateGameRequest;
import result.LoginRequest;

public class CreateGameServiceTests {

    @Test
    public void createGamePositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        var createGameRequest = new CreateGameRequest(loginResult.authToken(), "gameName");
        var createGameResult = createGameService.createGame(createGameRequest);

        Assertions.assertEquals(1, createGameResult.gameID());
    }

    @Test
    public void createGameNegative() throws  DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        var createGameRequest = new CreateGameRequest(loginResult.authToken(), null);

        Assertions.assertThrows(BadRequestException.class, () -> createGameService.createGame(createGameRequest));
    }
}
