package service;

import dataaccess.*;
import model.ClearResult;

public class ClearService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult clearDatabase() {
        authDAO.clear();
        userDAO.clear();
        gameDAO.clear();
        return new ClearResult(null);
    }
}
