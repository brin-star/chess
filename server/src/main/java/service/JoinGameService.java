package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import result.JoinGameRequest;
import result.JoinGameResult;

public class JoinGameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public JoinGameResult updateGame(JoinGameRequest joinGameRequest) throws DataAccessException {

        if (joinGameRequest.authToken() == null) {
            throw new UnauthorizedException("Missing token");
        }

        AuthData userToken = authDAO.getAuth(joinGameRequest.authToken());
        if (userToken == null) {
            throw new UnauthorizedException("Incorrect token");
        }

        if (joinGameRequest.playerColor() == null) {
            throw new BadRequestException("Missing player color");
        }

        GameData game = gameDAO.getGame(joinGameRequest.gameID());
        if (game == null) {
            throw new BadRequestException("Game not found");
        }

        if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) {
            throw new GameTakenException("Game already taken");
        }
        if (joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK && game.blackUsername() != null) {
            throw new GameTakenException("Game already taken");
        }

        gameDAO.updateGame(userToken.username(), joinGameRequest.playerColor(), game.gameID());

        return new JoinGameResult();
    }
}
