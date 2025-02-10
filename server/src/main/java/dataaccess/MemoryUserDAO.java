package dataaccess;

import java.util.HashMap;

import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> userDataMap = new HashMap<>();

    public MemoryUserDAO() {}

    @Override
    public void createUser(UserData userData) {
        userDataMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return userDataMap.get(username);
    }
}
