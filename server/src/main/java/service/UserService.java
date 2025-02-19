package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;
import model.requests.LogoutRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.LogoutResult;
import model.results.RegisterResult;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult createUser(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            return new RegisterResult(null, null, "Error: bad request");
        }

        UserData user = userDAO.getUser(registerRequest.username());
        if (user != null) {
            return new RegisterResult(null, null, "Error: already taken");
        }
        user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(user);
        AuthData authData = authDAO.createAuth(user);

        return new RegisterResult(user.username(), authData.authToken(), null);
    }

    public LoginResult loginUser(LoginRequest loginRequest) {
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null || !loginRequest.password().equals(user.password())) {
            return new LoginResult(null, null, "Error: unauthorized");
        }
        AuthData authData = authDAO.createAuth(user);

        return new LoginResult(user.username(), authData.authToken(), null);
    }

    public LogoutResult logoutUser(LogoutRequest logoutRequest) {
        if (!authDAO.deleteAuth(logoutRequest.authToken())) {
            return new LogoutResult("Error: unauthorized");
        }
        return new LogoutResult(null);
    }
}
