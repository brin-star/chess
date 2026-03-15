package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }



    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` VARCHAR(256) NOT NULL,
                `password` VARCHAR(256) NOT NULL,
                `email`    VARCHAR(256) NOT NULL,
                PRIMARY KEY (`username`)
            )
            """
    ;

    private void configureDatabase() throws DataAccessException {
        createDatabase();
        try (Connection conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
