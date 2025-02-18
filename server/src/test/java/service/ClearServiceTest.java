package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import model.results.ClearResult;

public class ClearServiceTest {
    @Test
    void testClearDatabase() throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        userDAO.createUser(new UserData("Test", "password", "test@example.com"));

        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        AuthData authData = authDAO.createAuth(new UserData("Test", "password", "test@example.com"));

        MemoryGameDAO gameDAO = new MemoryGameDAO();
        int gameID = gameDAO.createGame("Best Game!");

        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        Assertions.assertEquals(clearService.clearDatabase(), new ClearResult(null));
        Assertions.assertNull(userDAO.getUser("Test"));
        Assertions.assertNull(authDAO.getAuth(authData.authToken()));
        Assertions.assertNull(gameDAO.getGame(gameID));
    }
}
