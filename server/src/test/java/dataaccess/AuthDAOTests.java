package dataaccess;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dataaccess.sql.SQLAuthDAO;
import model.AuthData;
import model.UserData;

public class AuthDAOTests {
    private static AuthDAO authDAO;

    @BeforeAll
    public static void setupAuthDAO() {
        assertDoesNotThrow(() -> {
            authDAO = new SQLAuthDAO();
            authDAO.clear();
        });
    }

    @Test
    @AfterEach
    public void testClear() {
        assertDoesNotThrow(() -> {
            authDAO.clear();
        });
    }

    @Test
    void testCreateAuthGood() {
        final AuthData[] authData = new AuthData[1];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("Test", "password", "test@example.com"));
        });
        Assertions.assertNotNull(authData[0]);
    }

    @Test
    void testCreateAuthBad() {
        final AuthData[] authData = new AuthData[1];
        assertThrows(DataAccessException.class, () -> {
            authData[0] = authDAO.createAuth(new UserData(null, "password", "test@example.com"));
        });
        Assertions.assertNull(authData[0]);
    }

    @Test
    void testGetAuthGood() {
        final AuthData[] authData = new AuthData[2];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("Test", "password", "test@example.com"));
        });
        Assertions.assertNotNull(authData[0]);

        assertDoesNotThrow(() -> {
            authData[1] = authDAO.getAuth(authData[0].authToken());
        });
        Assertions.assertEquals(authData[0], authData[1]);
    }

    @Test
    void testGetAuthBad() {
        final AuthData[] authData = new AuthData[1];
        assertThrows(DataAccessException.class, () -> {
            authData[0] = authDAO.getAuth("invalidToken");
        });
        Assertions.assertNull(authData[0]);
    }

    @Test
    void testDeleteAuthGood() {
        final AuthData[] authData = new AuthData[1];
        assertDoesNotThrow(() -> {
            authData[0] = authDAO.createAuth(new UserData("Test", "password", "test@example.com"));
        });
        Assertions.assertNotNull(authData[0]);

        Assertions.assertTrue(authDAO.deleteAuth(authData[0].authToken()));
    }

    @Test
    void testDeleteAuthBad() {
        Assertions.assertFalse(authDAO.deleteAuth("invalidToken"));
    }
}
