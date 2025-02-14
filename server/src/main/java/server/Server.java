package server;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;

import com.google.gson.Gson;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);
    private final UserService userService = new UserService(authDAO, userDAO);

    public Server() {}

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clearHandler(Request req, Response res) {
        ClearResult result = clearService.clearDatabase();
        return new Gson().toJson(result);
    }

    private Object registerHandler(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.createUser(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private Object loginHandler(Request req, Response res) {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = userService.loginUser(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private Object logoutHandler(Request req, Response res) {
        LogoutRequest request = new LogoutRequest(req.headers("Authorization"));
        LogoutResult result = userService.logoutUser(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private Object listGamesHandler(Request req, Response res) {
        ListGamesRequest request = new ListGamesRequest(req.headers("Authorization"));
        ListGamesResult result = gameService.listGames(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private Object createGameHandler(Request req, Response res) {
        String gameName = new Gson().fromJson(req.body(), CreateGameRequest.class).gameName();
        CreateGameRequest request = new CreateGameRequest(req.headers("Authorization"), gameName);
        CreateGameResult result = gameService.createGame(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private Object joinGameHandler(Request req, Response res) {
        JoinGameRequest base = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameRequest request = new JoinGameRequest(req.headers("Authorization"), base.playerColor(), base.gameID());
        JoinGameResult result = gameService.joinGame(request);
        errorHandler(result.message(), res);
        return new Gson().toJson(result);
    }

    private void errorHandler(String message, Response res) {
        if (message == null) {
            return;
        }
        switch (message) {
            case "Error: already taken":
                res.status(403);
                break;
            case "Error: bad request":
                res.status(400);
                break;
            case "Error: unauthorized":
                res.status(401);
                break;
            default:
                res.status(500);
        }
    }
}
