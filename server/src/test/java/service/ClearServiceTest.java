package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.UserData;
import model.results.ClearResult;

public class ClearServiceTest {
    @Test
    void testClearDatabase() {
        UserDAO userDAO = new MemoryUserDAO();
        assertDoesNotThrow(() -> {
            userDAO.createUser(new UserData("Test", "password", "test@example.com"));
        });

        AuthDAO authDAO = new MemoryAuthDAO();
        final String[] authToken = new String[1];
        assertDoesNotThrow(() -> {
            authToken[0] = authDAO.createAuth(new UserData("Test", "password", "test@example.com")).authToken();
        });

        GameDAO gameDAO = new MemoryGameDAO();
        final Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("Best Game!");
        });

        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
        Assertions.assertEquals(clearService.clearDatabase(), new ClearResult(null));
        assertDoesNotThrow(() -> {
            Assertions.assertNull(userDAO.getUser("Test"));
            Assertions.assertNull(authDAO.getAuth(authToken[0]));
            Assertions.assertNull(gameDAO.getGame(gameID[0]));
        });
    }
}
