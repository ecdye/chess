package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.requests.CreateGameRequest;
import model.requests.JoinGameRequest;
import model.requests.ListGamesRequest;
import model.results.CreateGameResult;
import model.results.JoinGameResult;
import model.results.ListGamesResult;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

import java.util.Collection;

import chess.ChessMove;
import chess.InvalidMoveException;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        try {
            AuthData authData = authDAO.getAuth(createGameRequest.authToken());
            if (authData == null) {
                return new CreateGameResult(null, "Error: unauthorized");
            } else if (createGameRequest.gameName() == null) {
                return new CreateGameResult(null, "Error: bad request");
            }

            int gameID;
            gameID = gameDAO.createGame(createGameRequest.gameName());

            return new CreateGameResult(gameID, null);
        } catch (DataAccessException e) {
            return new CreateGameResult(null, "Error: " + e.getMessage());
        }
    }

    public ServerMessage makeMove(int gameID, ChessMove move) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            return new ErrorMessage(ERROR, "Error: no game");
        }

        try {
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            return new ErrorMessage(ERROR, "Error: invalid move");
        }

        gameDAO.updateGame(gameData);
        return new LoadGameMessage(LOAD_GAME, gameData.game());
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        try {
            AuthData authData = authDAO.getAuth(joinGameRequest.authToken());
            if (authData == null) {
                return new JoinGameResult("Error: unauthorized");
            }

            GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
            if (gameData == null || joinGameRequest.playerColor() == null) {
                return new JoinGameResult("Error: bad request");
            }

            gameData = setPlayerName(gameData, joinGameRequest.playerColor(), authData.username());
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            return new JoinGameResult("Error: " + e.getMessage());
        }

        return new JoinGameResult(null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        try {
            AuthData authData = authDAO.getAuth(listGamesRequest.authToken());
            if (authData == null) {
                throw new DataAccessException(null);
            }
        } catch (DataAccessException e) {
            return new ListGamesResult(null, "Error: unauthorized");
        }

        try {
            Collection<GameData> gamesList = gameDAO.listGames();
            return new ListGamesResult(gamesList, null);
        } catch (DataAccessException e) {
            return new ListGamesResult(null, "Error: " + e.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void setGame(GameData game) throws DataAccessException {
        gameDAO.updateGame(game);
    }

    private GameData setPlayerName(GameData gameData, String playerColor, String username) throws DataAccessException {
        switch (playerColor) {
            case "WHITE":
                if (gameData.whiteUsername() != null) {
                    throw new DataAccessException("already taken");
                }
                return new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(),
                        gameData.game());
            case "BLACK":
                if (gameData.blackUsername() != null) {
                    throw new DataAccessException("already taken");
                }
                return new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(),
                        gameData.game());
            default:
                throw new DataAccessException("bad request");
        }
    }
}
