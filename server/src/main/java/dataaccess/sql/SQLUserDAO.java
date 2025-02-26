package dataaccess.sql;

import java.sql.SQLException;
import java.sql.Connection;

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
        try {
            DatabaseManager.executeStatement(statement, userData.username(), password, userData.email());
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("already taken");
            }
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM userData WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            var result = DatabaseManager.queryStatement(conn, statement, username);
            if (result.next()) { // Check if there is data in the ResultSet
                String password = result.getString("password");
                String email = result.getString("email");
                return new UserData(username, password, email);
            } else {
                throw new DataAccessException("unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeStatement("TRUNCATE TABLE userData");
    }
}
