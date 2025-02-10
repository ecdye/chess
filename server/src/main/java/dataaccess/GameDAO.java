package dataaccess;

import java.util.Collection;

import model.GameData;

interface GameDAO {
    int createGame(String gameName);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
