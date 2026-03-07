package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import result.RegisterRequest;
import result.RegisterResult;

import java.util.UUID;

public class RegisterService {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    public RegisterService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Bad request");
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        userDAO.createUser(userData);
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, registerRequest.username());
        authDAO.createAuth(authData);
        return new RegisterResult(registerRequest.username(), token, null);
    }
}
