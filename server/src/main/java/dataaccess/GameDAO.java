package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(GameData gameData) throws DataAccessException;

    void clear();
}
