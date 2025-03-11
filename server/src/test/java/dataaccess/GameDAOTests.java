package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import dataaccess.sql.SQLGameDAO;
import model.GameData;

public class GameDAOTests {
    private static GameDAO gameDAO;

    @BeforeAll
    public static void setupGameDAO() {
        assertDoesNotThrow(() -> {
            gameDAO = new SQLGameDAO();
            gameDAO.clear();
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
    void testCreateGameGood() {
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("Test");
        });
        Assertions.assertNotNull(gameID[0]);
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
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("Test");
        });

        GameData[] gameData = new GameData[1];
        assertDoesNotThrow(() -> {
            gameData[0] = gameDAO.getGame(gameID[0]);
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
        Integer[] gameID = new Integer[1];
        assertDoesNotThrow(() -> {
            gameID[0] = gameDAO.createGame("Test");
        });

        assertDoesNotThrow(() -> {
            Collection<GameData> gameData = gameDAO.listGames();
            Assertions.assertNotNull(gameData);
            Assertions.assertEquals(1, gameData.size());
            Assertions.assertEquals(gameID[0], gameData.toArray(new GameData[0])[0].gameID());
        });
    }

    @Test
    void testListGamesEmpty() {
        assertDoesNotThrow(() -> {
            Collection<GameData> gameData = gameDAO.listGames();
            Assertions.assertNotNull(gameData);
            Assertions.assertEquals(0, gameData.size());
        });
    }

    @Test
    void testUpdateGame() {
        GameData gameData = testGetGameGood();
        GameData newGameData = new GameData(gameData.gameID(), "Test", gameData.blackUsername(), gameData.gameName(), gameData.game());
        assertDoesNotThrow(() -> {
            gameDAO.updateGame(newGameData);
        });
    }

    @Test
    void testUpdateGameEmpty() {
        GameData newGameData = new GameData(1234, "Test", null, "Test", new ChessGame());
        assertDoesNotThrow(() -> {
            gameDAO.updateGame(newGameData);
        });
        GameData[] gameData = new GameData[1];
        assertThrows(DataAccessException.class, () -> {
            gameData[0] = gameDAO.getGame(1234);
        });
        Assertions.assertNull(gameData[0]);
    }
}
