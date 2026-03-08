package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import result.LogoutRequest;
import result.LogoutResult;

public class LogoutService {
    private AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {

        if (logoutRequest.authToken() == null) {
            throw new UnauthorizedException("Missing token");
        }

        AuthData userToken = authDAO.getAuth(logoutRequest.authToken());
        if (userToken == null) {
            throw new UnauthorizedException("Incorrect token");
        }

        authDAO.deleteAuth(logoutRequest.authToken());
        return new LogoutResult(null);
    }
}
