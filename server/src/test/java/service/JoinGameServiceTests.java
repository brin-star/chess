package service;

import chess.ChessGame;
import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.CreateGameRequest;
import result.JoinGameRequest;
import result.LoginRequest;

public class JoinGameServiceTests {

    @Test
    public void joinGamePositive() throws DataAccessException {
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

        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);
        var joinGameRequest = new JoinGameRequest(loginResult.authToken(), createGameResult.gameID(), ChessGame.TeamColor.WHITE);
        var joinGameResult = joinGameService.updateGame(joinGameRequest);

        Assertions.assertNotNull(joinGameResult);
    }

    @Test
    public void joinGameNegative() throws DataAccessException {
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

        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);
        var joinGameRequest = new JoinGameRequest(loginResult.authToken(), createGameResult.gameID(), null);

        Assertions.assertThrows(BadRequestException.class, () -> joinGameService.updateGame(joinGameRequest));
    }
}
