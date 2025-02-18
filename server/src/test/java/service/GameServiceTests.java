package service;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import model.UserData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.ListGamesRequest;
import model.results.CreateGameResult;
import model.results.JoinGameResult;
import model.results.ListGamesResult;

public class GameServiceTests {
    private static GameService gameService;
    private static MemoryAuthDAO authDAO;
    private static String authToken;
    private static MemoryGameDAO gameDAO;

    @BeforeEach
    public void setupGameService() {
        authDAO = new MemoryAuthDAO();
        authToken = authDAO.createAuth(new UserData("Test", "password", "test@example.com")).authToken();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    void testCreateGameGood() {
        int gameID = gameService.createGame(new CreateGameRequest(authToken, "Best Game!")).gameID();

        GameData expectedGameData = new GameData(gameID, null, null, "Best Game!", new ChessGame());
        Assertions.assertEquals(expectedGameData, gameDAO.getGame(gameID));
    }

    @Test
    void testCreateGameBad() {
        CreateGameResult result = gameService.createGame(new CreateGameRequest(authToken, null));
        CreateGameResult expectedResult = new CreateGameResult(null, "Error: bad request");
        Assertions.assertEquals(expectedResult, result);

        authDAO.deleteAuth(authToken);
        result = gameService.createGame(new CreateGameRequest(authToken, "Best Game!"));
        expectedResult = new CreateGameResult(null, "Error: unauthorized");
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void testJoinGameGood() throws DataAccessException {
        int gameID = gameDAO.createGame("Best Game!");

        JoinGameResult result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));
        Assertions.assertEquals(new JoinGameResult(null), result);
    }

    @Test
    void testJoinGameBad() throws DataAccessException {
        int gameID = gameDAO.createGame("Best Game!");
        GameData gameData = gameDAO.getGame(gameID);
        gameData = new GameData(gameID, "TakenWhite", "TakenBlack", gameData.gameName(), gameData.game());
        gameDAO.updateGame(gameData);

        JoinGameResult result = gameService.joinGame(new JoinGameRequest(authToken, "WHITE", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: already taken"), result);

        result = gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: already taken"), result);

        result = gameService.joinGame(new JoinGameRequest(authToken, "BLUE", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: bad request"), result);

        authDAO.deleteAuth(authToken);
        result = gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameID));
        Assertions.assertEquals(new JoinGameResult("Error: unauthorized"), result);
    }

    @Test
    void testListGamesGood() throws DataAccessException {
        gameDAO.createGame("Best Game!");
        gameDAO.createGame("Average Game.");

        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));

        Collection<GameData> gamesList = gameDAO.listGames();
        Assertions.assertEquals(new ListGamesResult(gamesList, null), result);
    }

    @Test
    void testListGamesBad() throws DataAccessException {
        authDAO.deleteAuth(authToken);
        ListGamesResult result = gameService.listGames(new ListGamesRequest(authToken));

        Assertions.assertEquals(new ListGamesResult(null, "Error: unauthorized"), result);
    }
}
