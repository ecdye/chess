package dataaccess.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;

public class SQLUserDAO implements dataaccess.UserDAO {
    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String statement = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        String password = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        DatabaseManager.executeStatement(statement, userData.username(), password, userData.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM userData WHERE username = ?";
        ResultSet result = DatabaseManager.queryStatement(statement, username);
        try {
            String password = result.getString("password");
            String email = result.getString("email");
            return new UserData(username, password, email);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeStatement("TRUNCATE TABLE userData", (Object[]) null);
    }
}
