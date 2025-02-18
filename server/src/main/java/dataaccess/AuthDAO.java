package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData);

    AuthData getAuth(String authToken);

    Boolean deleteAuth(String authToken);

    void clear();
}
