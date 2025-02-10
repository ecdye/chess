package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.AuthData;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authDataMap = new HashMap<>();

    public MemoryAuthDAO() {}

    @Override
    public AuthData createAuth(UserData userData) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDataMap.put(authToken, authData);

        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    @Override
    public Boolean deleteAuth(String authToken) {
        AuthData authData = authDataMap.remove(authToken);
        return authData != null;
    }

    @Override
    public void clear() {
        authDataMap.clear();
    }
}
