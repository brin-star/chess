package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth: " + e.getMessage());
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "SELECT token, username FROM auth_tokens WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String token = rs.getString("token");
                        String user = rs.getString("username");
                        return new AuthData(token, user);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get token: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "DELETE FROM auth_tokens WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete token: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "TRUNCATE auth_tokens";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth_tokens: " + e.getMessage());
        }
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
