package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates all the needed tables if they don't exist
     *
     * @throws DataAccessException
     */
    static void createTables() throws DataAccessException {
        var authStatement = """
                CREATE TABLE IF NOT EXISTS authData (
                    username varchar(256) NOT NULL,
                    authToken varchar(256) NOT NULL UNIQUE,
                    PRIMARY KEY (authToken)
                )
                """;
        var userStatement = """
                CREATE TABLE IF NOT EXISTS userData (
                    username varchar(256) NOT NULL UNIQUE,
                    password varchar(256) NOT NULL,
                    email varchar(256) NOT NULL UNIQUE,
                    PRIMARY KEY (username)
                )
                """;
        var gameStatement = """
                CREATE TABLE IF NOT EXISTS gameData (
                    gameID int NOT NULL UNIQUE,
                    whiteUsername varchar(256),
                    blackUsername varchar(256),
                    gameName varchar(256) NOT NULL,
                    game LONGTEXT NOT NULL,
                    PRIMARY KEY (gameID)
                )
                """;
        String[] statements = { authStatement, userStatement, gameStatement };
        try (var conn = getConnection()) {
            for (var statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Configure and setup database for chess. One stop shop to ensure we are
     * ready to go!
     *
     * @throws DataAccessException
     */
    public static void configureDatabase() throws DataAccessException {
        createDatabase();
        createTables();
    }

    public static void executeStatement(String string, Object... params) throws DataAccessException {
        try (var conn = getConnection()) {
            var preparedStatement = conn.prepareStatement(string);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static ResultSet queryStatement(Connection conn, String string, Object... params)
            throws DataAccessException {
        try {
            var preparedStatement = conn.prepareStatement(string);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            ResultSet result = preparedStatement.executeQuery();
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
