package client;

import ServerFacade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void resetDB() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void positiveClearTest() throws Exception {
        facade.register("username", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void positiveRegisterTest() throws Exception {
        var result = facade.register("username", "password", "email@email.com");

        Assertions.assertNotNull(result.authToken());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void negativeRegisterTest() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.register("username", "password", "email@email.com");
            facade.register("username", "password", "email@email.com");
        });
    }

    @Test
    public void positiveLoginTest() throws Exception {
        facade.register("username", "password", "email@email.com");
        var result = facade.login("username", "password");

        Assertions.assertNotNull(result.authToken());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void negativeLoginTest() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.login("username", "password");
        });
    }

    @Test
    public void positiveLogoutTest() throws Exception {
        facade.register("username", "password", "email@email.com");
        var session = facade.login("username", "password");

        Assertions.assertDoesNotThrow(() -> {
            facade.logout(session.authToken());
        });
    }

    @Test
    public void negativeLogoutTest() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.logout("nonexistant");
        });
    }

    @Test
    public void positiveCreateGameTest() throws Exception {
        facade.register("username", "password", "email@email.com");
        var session = facade.login("username", "password");
        var result = facade.createGame(session.authToken(), "Game Name");

        Assertions.assertNotNull(result.gameID());
    }

    @Test
    public void negativeCreateGameTest() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame("nonexistent", "Game Name");
        });
    }

    @Test
    public void positiveListGamesTest() throws Exception {
        facade.register("username", "password", "email@email.com");
        var session = facade.login("username", "password");
        facade.createGame(session.authToken(), "Game Name");
        var result = facade.listGames(session.authToken());

        Assertions.assertTrue(result.games().size() == 1);
    }

    @Test
    public void negativeListGameTest() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames("nonexistent");
        });
    }
}
