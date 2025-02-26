package dataaccess.memory;

import model.UserData;

import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;

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
        String password = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        userDataMap.put(userData.username(), new UserData(userData.username(), password, userData.email()));
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
