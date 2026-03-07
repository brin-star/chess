package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import result.RegisterRequest;

public class RegisterServiceTests {

    @Test
    public void registerPositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        RegisterService service = new RegisterService(authDAO, userDAO);
        var request = new RegisterRequest("superman", "kryptonite", "clarkkent@dailyplanet.com");
        var result = service.register(request);

        Assertions.assertEquals("superman", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    public void registerNegative() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        RegisterService service = new RegisterService(authDAO, userDAO);
        var request = new RegisterRequest("batman", "imbatman", null);

        Assertions.assertThrows(BadRequestException.class, () -> service.register(request));
    }
}
