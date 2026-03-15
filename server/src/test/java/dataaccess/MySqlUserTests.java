package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserTests {

    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;
    private UserData testUser = new UserData("test", "testPassword", "test@email.com");

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
    public void createUserPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData result = userDAO.getUser("test");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.username());
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        userDAO.createUser(testUser);

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(testUser);
        });
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData result = userDAO.getUser("test");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testUser.username(), result.username());
        Assertions.assertTrue(BCrypt.checkpw(testUser.password(), result.password()));
        Assertions.assertEquals(testUser.email(), result.email());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData result = userDAO.getUser("doesntExist");

        Assertions.assertNull(result);
    }

    @Test
    public void clearPositive() throws DataAccessException {
        userDAO.createUser(testUser);
        userDAO.clear();
        UserData result = userDAO.getUser("test");

        Assertions.assertNull(result);
    }
}
