package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(String userName, String playerColor, int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}
