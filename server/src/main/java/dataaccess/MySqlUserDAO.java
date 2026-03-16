package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.*;

public class MySqlUserDAO implements UserDAO {

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

    public MySqlUserDAO() throws DataAccessException {
        MySqlBaseDAO.configureDatabase(createStatement);
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String user = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(user, password, email);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user: " + e.getMessage());
        }
    }

    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "DELETE FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear users: " + e.getMessage());
        }
    }
}
