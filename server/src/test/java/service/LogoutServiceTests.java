package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.LoginRequest;
import result.LogoutRequest;

public class LogoutServiceTests {

    @Test
    public void logoutPositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        LogoutService logoutService = new LogoutService(authDAO);
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        var logoutResult = logoutService.logout(logoutRequest);

        Assertions.assertNull(logoutResult.message());
    }

    @Test
    public void logoutNegative() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService loginService = new LoginService(authDAO, userDAO);
        var loginRequest = new LoginRequest("username", "password");
        var loginResult = loginService.login(loginRequest);

        LogoutService logoutService = new LogoutService(authDAO);
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        logoutService.logout(logoutRequest);

        Assertions.assertThrows(UnauthorizedException.class, () -> logoutService.logout(logoutRequest));
    }
}
