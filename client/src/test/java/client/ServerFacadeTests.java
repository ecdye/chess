package client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.*;

import model.results.*;
import server.Server;
import server.ServerFacade;


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
    @BeforeEach
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
        assertDoesNotThrow(() -> {
            result[0] = facade.register("Test", null, "test@example.com");
        });
        Assertions.assertNotNull(result[0].message());
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
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            result[0] = facade.login("Test", "badPassword");
        });
        Assertions.assertNotNull(result[0].message());
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
        assertDoesNotThrow(() -> {
            result[0] = facade.logout();
        });
        Assertions.assertNotNull(result[0].message());
    }

    @Test
    public void testCreateGameGood() {
        final CreateGameResult[] result = new CreateGameResult[1];
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            result[0] = facade.createGame("Test");
        });
        Assertions.assertNotNull(result[0].gameID());
    }

    @Test
    public void testCreateGameBad() {
        final CreateGameResult[] result = new CreateGameResult[1];
        assertDoesNotThrow(() -> {
            result[0] = facade.createGame("Test");
        });
        Assertions.assertNotNull(result[0].message());
    }

    @Test
    public void testJoinGameGood() {
        final JoinGameResult[] result = new JoinGameResult[1];
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            int gameID = facade.createGame("Test").gameID();
            result[0] = facade.joinGame("WHITE", gameID);
        });
        Assertions.assertNull(result[0].message());
    }

    @Test
    public void testJoinGameBad() {
        final JoinGameResult[] result = new JoinGameResult[1];
        assertDoesNotThrow(() -> {
            facade.register("Test", "password", "test@example.com");
            int gameID = facade.createGame("Test").gameID();
            facade.joinGame("WHITE", gameID);
            result[0] = facade.joinGame("WHITE", gameID);
        });
        Assertions.assertNotNull(result[0].message());
    }
}
