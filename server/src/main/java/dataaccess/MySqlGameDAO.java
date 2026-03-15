package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }



    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID`        INT NOT NULL AUTO_INCREMENT,
                `whiteUsername` VARCHAR(256),
                `blackUsername` VARCHAR(256),
                `gameName`      VARCHAR(256) NOT NULL,
                `gameState`     TEXT NOT NULL,
                PRIMARY KEY (`gameID`),
                FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`),
                FOREIGN KEY (`blackUsername`) REFERENCES users(`username`)
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
