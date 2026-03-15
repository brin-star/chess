package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlUserTests {

    @BeforeEach
    public void clearTable(){ MySqlUserDAO().clear(); };

    @Test
    public void createUserPositive() {
        UserData testUser = new UserData("test", "testPassword", "test@email.com");
        var result = createUser(testUser);

        Assertions.assertEquals(new UserData(testUser), testUser);
    }
}
