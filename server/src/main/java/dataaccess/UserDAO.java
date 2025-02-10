package dataaccess;

import model.UserData;

interface UserDAO {
    void createUser(UserData userData);
    UserData getUser(String username);
    void clear();
}
