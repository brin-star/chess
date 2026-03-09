package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.ListGamesRequest;
import result.LoginRequest;
import result.LogoutRequest;

public class ListGamesServiceTests {

    @Test
    public void listGamesPositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        var listGamesRequest = new ListGamesRequest(loginResult.authToken());
        var listGamesResult = listGamesService.listGames(listGamesRequest);

        Assertions.assertEquals(0, listGamesResult.games().size());
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        LogoutService logoutService = new LogoutService(authDAO);
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        logoutService.logout(logoutRequest);

        ListGamesService listGamesService = new ListGamesService(gameDAO, authDAO);
        var listGamesRequest = new ListGamesRequest(loginResult.authToken());

        Assertions.assertThrows(UnauthorizedException.class, () -> listGamesService.listGames(listGamesRequest));
    }
}
