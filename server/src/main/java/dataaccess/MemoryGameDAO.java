package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(String userName, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        GameData existingGame = getGame(gameID);
        GameData newGame;

        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            newGame = new GameData(gameID, userName, existingGame.blackUsername(), existingGame.gameName(), existingGame.game());
        }
        else {
            newGame = new GameData(gameID, existingGame.whiteUsername(), userName, existingGame.gameName(), existingGame.game());
        }
        games.put(newGame.gameID(), newGame);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public void updateGameInDB(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }
}
