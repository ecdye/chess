package dataaccess;

import model.UserData;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(UserData userData);
    AuthData getAuth(String authToken);
    Boolean deleteAuth(String authToken);
    void clear();
}
