package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData createAuth(UserData userData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    Boolean deleteAuth(String authToken);

    void clear() throws DataAccessException;
}
