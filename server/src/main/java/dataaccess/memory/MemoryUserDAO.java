package dataaccess.memory;

import model.UserData;

import java.util.HashMap;

import dataaccess.DataAccessException;

public class MemoryUserDAO implements dataaccess.UserDAO {
    private HashMap<String, UserData> userDataMap = new HashMap<>();

    public MemoryUserDAO() {
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (userDataMap.containsKey(userData.username())) {
            throw new DataAccessException("already taken");
        }
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }

    @Override
    public void clear() {
        userDataMap.clear();
    }
}
