package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import result.LoginRequest;
import result.LoginResult;

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
        if (!BCrypt.checkpw(loginRequest.password(), requestedUser.password())) {
            throw new UnauthorizedException("Incorrect login");
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, loginRequest.username());
        authDAO.createAuth(authData);
        return new LoginResult(loginRequest.username(), token, null);
    }
}
