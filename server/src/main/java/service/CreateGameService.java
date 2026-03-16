package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import result.CreateGameRequest;
import result.CreateGameResult;

import java.util.UUID;

public class CreateGameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {

        if (createGameRequest.authToken() == null) {
            throw new UnauthorizedException("Missing token");
        }

        AuthData userToken = authDAO.getAuth(createGameRequest.authToken());
        if (userToken == null) {
            throw new UnauthorizedException("Incorrect token");
        }

        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Missing game name");
        }

        int gameID = Math.abs(UUID.randomUUID().hashCode());

        ChessGame game = new ChessGame();
        GameData newGame = new GameData(gameID, null, null, createGameRequest.gameName(), game);
        gameDAO.createGame(newGame);

        return new CreateGameResult(gameID);
    }
}
