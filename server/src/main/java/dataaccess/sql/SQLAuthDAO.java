package dataaccess.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;

public class SQLAuthDAO implements dataaccess.AuthDAO {
    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        deleteAuth(authToken);
        String statement = "INSERT INTO authData (username, authToken) VALUES (?, ?)";
        DatabaseManager.executeStatement(statement, userData.username(), authToken);
        return new AuthData(authToken, userData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT * FROM authData WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            var result = DatabaseManager.queryStatement(conn, statement, authToken);
            if (result.next()) { // Check if there is data in the ResultSet
                String username = result.getString("username");
                return new AuthData(authToken, username);
            } else {
                throw new DataAccessException("unauthorized");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Boolean deleteAuth(String authToken) {
        String statement = "DELETE FROM authData WHERE authToken = ?";
        try {
            getAuth(authToken);
            DatabaseManager.executeStatement(statement, authToken);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeStatement("TRUNCATE TABLE authData");
    }
}
