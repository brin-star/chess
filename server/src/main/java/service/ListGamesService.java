package service;

import dataaccess.*;
import model.AuthData;
import result.ListGamesRequest;
import result.ListGamesResult;

public class ListGamesService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ListGamesService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {

        if (listGamesRequest.authToken() == null) {
            throw new UnauthorizedException("Missing token");
        }

        AuthData userToken = authDAO.getAuth(listGamesRequest.authToken());
        if (userToken == null) {
            throw new UnauthorizedException("Incorrect token");
        }

        return new ListGamesResult(gameDAO.listGames());
    }
}
