package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlAuthTests {

    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;
    private UserData testUser = new UserData("test", "testPassword", "test@email.com");
    private AuthData testAuth = new AuthData("testToken", "test");

    @BeforeEach
    public void clearTable() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        authDAO.createAuth(testAuth);
        AuthData result = authDAO.getAuth("testToken");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testToken", result.authToken());
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        userDAO.createUser(testUser);
        authDAO.createAuth(testAuth);

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(testAuth);
        });
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        authDAO.createAuth(testAuth);
        AuthData result = authDAO.getAuth("testToken");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testAuth.authToken(), result.authToken());
        Assertions.assertEquals(testAuth.username(), result.username());
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData result = authDAO.getAuth("doesntExist");

        Assertions.assertNull(result);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        authDAO.createAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        AuthData result = authDAO.getAuth("testToken");

        Assertions.assertNull(result);
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        userDAO.createUser(testUser);

        Assertions.assertDoesNotThrow(() -> {
            authDAO.deleteAuth(testAuth.authToken());
        });
    }

    @Test
    public void clearPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        authDAO.createAuth(testAuth);
        authDAO.clear();
        AuthData result = authDAO.getAuth("testToken");

        Assertions.assertNull(result);
    }
}
