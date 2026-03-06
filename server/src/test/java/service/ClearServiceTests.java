package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    @Test
    public void clearPositive() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();
        ClearService service = new ClearService(authDAO, gameDAO, userDAO);

        Assertions.assertDoesNotThrow(service::clear);
    }
}
