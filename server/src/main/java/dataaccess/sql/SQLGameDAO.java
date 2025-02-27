package dataaccess.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.GameData;

public class SQLGameDAO implements dataaccess.GameDAO {
    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM gameData WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            var result = DatabaseManager.queryStatement(conn, statement, gameID);
            if (result.next()) { // Check if there is data in the ResultSet
                String whiteUsername = result.getString("whiteUsername");
                String blackUsername = result.getString("blackUsername");
                String gameName = result.getString("gameName");
                ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            } else {
                throw new DataAccessException("bad request");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String statement = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
        String gameString = new Gson().toJson(gameData.game());

        DatabaseManager.executeStatement(statement, gameData.whiteUsername(), gameData.blackUsername(),
                gameString, gameData.gameID());
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = new Random().nextInt(1000, 10000);

        String gameString = new Gson().toJson(new ChessGame());
        String statement = "INSERT INTO gameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        DatabaseManager.executeStatement(statement, gameID, null, null, gameName, gameString);

        return gameID;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String statement = "SELECT * FROM gameData";
        try (Connection conn = DatabaseManager.getConnection()) {
            var result = DatabaseManager.queryStatement(conn, statement);
            ArrayList<GameData> games = new ArrayList<>();
            while (result.next()) {
                int gameID = result.getInt("gameID");
                String whiteUsername = result.getString("whiteUsername");
                String blackUsername = result.getString("blackUsername");
                String gameName = result.getString("gameName");
                ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }
            return games;
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeStatement("TRUNCATE TABLE gameData");
    }
}
