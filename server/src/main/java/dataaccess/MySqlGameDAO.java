package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dataaccess.DatabaseManager.*;

public class MySqlGameDAO implements GameDAO{

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createGame(GameData gameData) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String gameJson = new Gson().toJson(gameData.game());
                ps.setInt(1, gameData.gameID());
                ps.setString(2, gameData.whiteUsername());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.gameName());
                ps.setString(5, gameJson);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                    else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game: " + e.getMessage());
        }
    }

    public List<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game list: " + e.getMessage());
        }
        return result;
    }

    private GameData readGame(ResultSet rs) throws DataAccessException, SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameState = rs.getString("gameState");
        ChessGame game = new Gson().fromJson(gameState, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public void updateGame(String userName, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {

    }

    public void clear() throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "TRUNCATE games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear games: " + e.getMessage());
        }
    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
                `gameID`        INT NOT NULL,
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
