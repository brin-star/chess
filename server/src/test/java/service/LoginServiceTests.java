package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.LoginRequest;

public class LoginServiceTests {

    @Test
    public void loginPositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService service = new LoginService(authDAO, userDAO);
        var request = new LoginRequest("username", "password");
        var result = service.login(request);

        Assertions.assertEquals("username", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void loginNegative() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();

        UserData userData = new UserData("username", "password", "email");
        userDAO.createUser(userData);

        LoginService service = new LoginService(authDAO, userDAO);
        var request = new LoginRequest("username", "wrong");

        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(request));
    }
}
