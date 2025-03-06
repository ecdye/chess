package client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.*;

import model.requests.LogoutRequest;
import model.results.LoginResult;
import model.results.LogoutResult;
import model.results.RegisterResult;
import server.Server;
import server.ServerFacade;
import server.ServerFacadeException;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @AfterEach
    public void testClear() {
        assertDoesNotThrow(() -> {
            facade.clearDatabase();
        });
    }


    @Test
    public void testRegisterGood() {
        final RegisterResult[] result = new RegisterResult[1];
        assertDoesNotThrow(() -> {
            result[0] = facade.register("Test", "password", "test@example.com");
        });
        Assertions.assertEquals(UUID.fromString(result[0].authToken()).toString(), result[0].authToken());
    }

    @Test
    public void testRegisterBad() {
        final RegisterResult[] result = new RegisterResult[1];
        assertThrows(ServerFacadeException.class, () -> {
            result[0] = facade.register("Test", null, "test@example.com");
        });
        Assertions.assertNull(result[0]);
    }

    @Test
    public void testLoginGood() {
        final LoginResult[] result = new LoginResult[1];
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            result[0] = facade.login("Test", "password");
        });
        Assertions.assertEquals(UUID.fromString(result[0].authToken()).toString(), result[0].authToken());
    }

    @Test
    public void testLoginBad() {
        final LoginResult[] result = new LoginResult[1];
        assertThrows(ServerFacadeException.class, () -> {
            facade.register("Test", "password", "test@example.com");
            result[0] = facade.login("Test", "badPassword");
        });
        Assertions.assertNull(result[0]);
    }

    @Test
    public void testLogoutGood() {
        final LogoutResult[] result = new LogoutResult[1];
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            result[0] = facade.logout();
        });
        Assertions.assertNull(result[0].message());
    }

    @Test
    public void testLogoutBad() {
        final LogoutResult[] result = new LogoutResult[1];
        assertThrows(ServerFacadeException.class, () -> {
            result[0] = facade.logout();
        });
        Assertions.assertNull(result[0]);
    }
}
