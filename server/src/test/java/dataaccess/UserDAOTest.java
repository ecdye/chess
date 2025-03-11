package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dataaccess.sql.SQLUserDAO;
import model.UserData;

public class UserDAOTest {
    private static UserDAO userDAO;

    @BeforeAll
    public static void setupUserDAO() {
        assertDoesNotThrow(() -> {
            userDAO = new SQLUserDAO();
            userDAO.clear();
        });
    }

    @Test
    @AfterEach
    public void clearTest() {
        assertDoesNotThrow(() -> {
            userDAO.clear();
        });
    }

    @Test
    void createUserGood() {
        assertDoesNotThrow(() -> {
            userDAO.createUser(new UserData("Test", "password", "test@example.com"));
        });
    }

    @Test
    void createUserBad() {
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData(null, "password", "test@example.com"));
        });
    }

    @Test
    void getUserGood() {
        createUserGood();

        final UserData[] userData = new UserData[1];
        assertDoesNotThrow(() -> {
            userData[0] = userDAO.getUser("Test");
        });
        Assertions.assertNotNull(userData[0]);
    }

    @Test
    void getUserBad() {
        final UserData[] userData = new UserData[1];
        assertThrows(DataAccessException.class, () -> {
            userData[0] = userDAO.getUser("Test");
        });
        Assertions.assertNull(userData[0]);
    }
}
