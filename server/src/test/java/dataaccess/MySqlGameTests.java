package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MySqlGameTests {

    private MySqlUserDAO userDAO;
    private MySqlAuthDAO authDAO;
    private MySqlGameDAO gameDAO;
    private UserData whiteTest = new UserData("whiteTest", "testPassword", "white@email.com");
    private UserData blackTest = new UserData("blackTest", "testPassword", "black@email.com");
    private GameData testGame = new GameData(1, "whiteTest", "blackTest", "testGame", new ChessGame());

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
    public void createGamePositive() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);
        GameData result = gameDAO.getGame(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.gameID());
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(testGame);
        });
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);
        GameData result = gameDAO.getGame(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.gameID());
        Assertions.assertEquals(testGame.whiteUsername(), result.whiteUsername());
        Assertions.assertEquals(testGame.blackUsername(), result.blackUsername());
        Assertions.assertEquals(testGame.gameName(), result.gameName());
        Assertions.assertEquals(testGame.game(), result.game());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData result = gameDAO.getGame(1);

        Assertions.assertNull(result);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);
        List<GameData> result = gameDAO.listGames();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(testGame, result.get(0));
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        List<GameData> result = gameDAO.listGames();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        GameData game = new GameData(1, "whiteTest", null, "testGame", new ChessGame());
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(game);
        gameDAO.updateGame(blackTest.username(), ChessGame.TeamColor.BLACK, game.gameID());
        var result = gameDAO.getGame(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.gameID());
        Assertions.assertEquals(game.whiteUsername(), result.whiteUsername());
        Assertions.assertEquals(blackTest.username(), result.blackUsername());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(blackTest.username(), ChessGame.TeamColor.BLACK, 9999);
        });
    }

    @Test
    public void clearPositive() throws DataAccessException {
        userDAO.createUser(whiteTest);
        userDAO.createUser(blackTest);
        gameDAO.createGame(testGame);
        gameDAO.clear();
        GameData result = gameDAO.getGame(1);

        Assertions.assertNull(result);
    }
}
