package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.results.ClearResult;

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
        try {
            authDAO.clear();
            userDAO.clear();
            gameDAO.clear();
            return new ClearResult(null);
        } catch (DataAccessException e) {
            return new ClearResult("Error:" + e);
        }
    }
}
