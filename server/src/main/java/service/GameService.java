package service;

import java.util.Collection;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.JoinGameRequest;
import model.JoinGameResult;
import model.ListGamesRequest;
import model.ListGamesResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        AuthData authData = authDAO.getAuth(createGameRequest.authToken());
        if (authData == null) {
            return new CreateGameResult(null, "Error: unauthorized");
        } else if (createGameRequest.gameName() == null) {
            return new CreateGameResult(null, "Error: bad request");
        }

        int gameID;
        try {
            gameID = gameDAO.createGame(createGameRequest.gameName());
        } catch (DataAccessException e) {
            return new CreateGameResult(null, "Error: " + e);
        }

        return new CreateGameResult(gameID, null);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        AuthData authData = authDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            return new JoinGameResult("Error: unauthorized");
        }

        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null || joinGameRequest.playerColor() == null) {
            return new JoinGameResult("Error: bad request");
        }

        switch (joinGameRequest.playerColor()) {
            case "WHITE":
                if (gameData.whiteUsername() != null) {
                    return new JoinGameResult("Error: already taken");
                }
                gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                break;
            case "BLACK":
                if (gameData.blackUsername() != null) {
                    return new JoinGameResult("Error: already taken");
                }
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                break;
            default:
                return new JoinGameResult("Error: bad request");
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            return new JoinGameResult("Error: " + e);
        }

        return new JoinGameResult(null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            return new ListGamesResult(null, "Error: unauthorized");
        }

        Collection<GameData> gamesList = gameDAO.listGames();
        return new ListGamesResult(gamesList, null);
    }
}
