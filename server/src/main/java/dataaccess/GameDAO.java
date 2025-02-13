package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
