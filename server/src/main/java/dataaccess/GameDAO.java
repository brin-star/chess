package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(String userName, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}
