package dataaccess;

import java.util.HashMap;
import java.util.Collection;
import java.util.Random;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    public MemoryGameDAO() {}

	@Override
	public GameData getGame(int gameID) {
		return gameDataMap.get(gameID);
	}

	@Override
	public void updateGame(GameData gameData) throws DataAccessException {
		if (!gameDataMap.containsKey(gameData.gameID())) {
            throw new DataAccessException("Error: No game with the given gameID");
        }
        gameDataMap.put(gameData.gameID(), gameData);
	}

	@Override
	public int createGame(String gameName) throws DataAccessException {
		int gameID = new Random().nextInt(1000, 10000);
		int i = 0;
		while (gameDataMap.containsKey(gameID) && i < 5) {
            gameID = new Random().nextInt(1000, 10000);
			i++;
        }
		if (i == 5 && gameDataMap.containsKey(gameID)) {
			throw new DataAccessException("Error: Could not obtain unique gameID");
		}
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDataMap.put(gameID, gameData);

        return gameID;
	}

	@Override
	public Collection<GameData> listGames() {
		return gameDataMap.values();
	}

    @Override
    public void clear() {
        gameDataMap.clear();
    }
}
