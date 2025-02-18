package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import model.requests.LoginRequest;
import model.requests.LogoutRequest;
import model.requests.RegisterRequest;
import model.results.LoginResult;
import model.results.LogoutResult;
import model.results.RegisterResult;

public class UserServiceTests {
    private static MemoryAuthDAO authDAO;
    private static MemoryUserDAO userDAO;
    private static UserService userService;

    @BeforeEach
    public void setupUserService() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        userService = new UserService(authDAO, userDAO);
    }

    @Test
    void testCreateUserGood() {
        RegisterResult result = userService.createUser(new RegisterRequest("Test", "password", "test@example.com"));
        Assertions.assertNotNull(result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNull(result.message());
    }

    @Test
    void testCreateUserBad() {
        RegisterResult result = userService.createUser(new RegisterRequest(null, null, null));
        Assertions.assertEquals(new RegisterResult(null, null, "Error: bad request"), result);

        userDAO.createUser(new UserData("Test", "password", "test@example.com"));
        result = userService.createUser(new RegisterRequest("Test", "password", "test@example.com"));
        Assertions.assertEquals(new RegisterResult(null, null, "Error: already taken"), result);
    }

    @Test
    void testLoginUserGood() {
        userDAO.createUser(new UserData("Test", "password", "test@example.com"));
        LoginResult result = userService.loginUser(new LoginRequest("Test", "password"));
        Assertions.assertNotNull(result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNull(result.message());
    }

    @Test
    void testLoginUserBad() {
        userDAO.createUser(new UserData("Test", "password", "test@example.com"));
        LoginResult result = userService.loginUser(new LoginRequest("Test", "incorrect"));
        Assertions.assertEquals(new LoginResult(null, null, "Error: unauthorized"), result);
    }

    @Test
    void testLogoutUserGood() {
        String authToken = authDAO.createAuth(new UserData("Test", "password", "test@example.com")).authToken();
        LogoutResult result = userService.logoutUser(new LogoutRequest(authToken));
        Assertions.assertEquals(new LogoutResult(null), result);
    }

    @Test
    void testLogoutUserBad() {
        String authToken = "invalid authToken";
        LogoutResult result = userService.logoutUser(new LogoutRequest(authToken));
        Assertions.assertEquals(new LogoutResult("Error: unauthorized"), result);
    }
}
