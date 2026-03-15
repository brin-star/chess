package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }



    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS auth_tokens (
                `token`    VARCHAR(256) NOT NULL,
                `username` VARCHAR(256) NOT NULL,
                PRIMARY KEY (`token`),
                FOREIGN KEY (`username`) REFERENCES users(`username`)
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
}
