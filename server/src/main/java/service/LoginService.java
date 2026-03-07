package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import result.LoginRequest;
import result.LoginResult;

import java.util.Objects;
import java.util.UUID;

public class LoginService {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    public LoginService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException("Bad request");
        }

        UserData requestedUser = userDAO.getUser(loginRequest.username());
        if (requestedUser == null) {
            throw new UnauthorizedException("Incorrect login");
        }
        if (!Objects.equals(requestedUser.password(), loginRequest.password())) {
            throw new UnauthorizedException("Incorrect login");
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, loginRequest.username());
        authDAO.createAuth(authData);
        return new LoginResult(loginRequest.username(), token, null);
    }
}
