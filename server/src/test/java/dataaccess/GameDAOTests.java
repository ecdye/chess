package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dataaccess.sql.SQLGameDAO;
import model.GameData;

public class GameDAOTests {
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setupGameDAO() {
        assertDoesNotThrow(() -> {
            gameDAO = new SQLGameDAO();
        });
    }

    @Test
    @AfterEach
    public void testClear() {
        assertDoesNotThrow(() -> {
            gameDAO.clear();
        });
    }

    @Test
    int testCreateGameGood() {
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("Test");
        });
        Assertions.assertNotNull(gameID[0]);
        return gameID[0];
    }

    @Test
    void testCreateGameBad() {
        Integer[] gameID = new Integer[1];
        assertThrows(DataAccessException.class, () -> {
            gameID[0] = gameDAO.createGame(null);
        });
        Assertions.assertNull(gameID[0]);
    }

    @Test
    GameData testGetGameGood() {
        int gameID = testCreateGameGood();

        GameData[] gameData = new GameData[1];
        assertDoesNotThrow(() -> {
            gameData[0] = gameDAO.getGame(gameID);
        });
        Assertions.assertNotNull(gameData[0]);
        return gameData[0];
    }

    @Test
    void testGetGameBad() {
        GameData[] gameData = new GameData[1];
        assertThrows(DataAccessException.class, () -> {
            gameData[0] = gameDAO.getGame(1111);
        });
        Assertions.assertNull(gameData[0]);
    }

    @Test
    void testListGames() {
        int gameID = testCreateGameGood();

        assertDoesNotThrow(() -> {
            Collection<GameData> gameData = gameDAO.listGames();
            Assertions.assertNotNull(gameData);
            Assertions.assertEquals(1, gameData.size());
            Assertions.assertEquals(gameID, gameData.toArray(new GameData[0])[0].gameID());
        });
    }

    @Test
    GameData testUpdateGame() {
        GameData gameData = testGetGameGood();
        GameData newGameData = new GameData(gameData.gameID(), "Test", gameData.blackUsername(), gameData.gameName(), gameData.game());
        assertDoesNotThrow(() -> {
            gameDAO.updateGame(newGameData);
        });
        return newGameData;
    }
}
