package server;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;

import model.requests.*;
import model.results.*;

public class ServerFacade {
    private final String serverURL;
    public String authToken;

    public ServerFacade(int port) {
        serverURL = "http://localhost:" + port;
    }

    public ClearResult clearDatabase() throws ServerFacadeException {
        return makeRequest("DELETE", "/db", null, ClearResult.class);
    }

    public RegisterResult register(String username, String password, String email) throws ServerFacadeException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = makeRequest("POST", "/user", request, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    public LoginResult login(String username, String password) throws ServerFacadeException {
        LoginRequest request = new LoginRequest(username, password);
        LoginResult result = makeRequest("POST", "/session", request, LoginResult.class);
        authToken = result.authToken();
        return result;
    }

    public LogoutResult logout() throws ServerFacadeException {
        LogoutResult result = makeRequest("DELETE", "/session", null, LogoutResult.class);
        authToken = null;
        return result;
    }

    public ListGamesResult listGames() throws ServerFacadeException {
        return makeRequest("GET", "/game", null, ListGamesResult.class);
    }

    public CreateGameResult createGame(String gameName) throws ServerFacadeException {
        CreateGameRequest request = new CreateGameRequest(null, gameName);
        return makeRequest("POST", "/game", request, CreateGameResult.class);
    }

    public JoinGameResult joinGame(String playerColor, int gameID) throws ServerFacadeException {
        JoinGameRequest request = new JoinGameRequest(null, playerColor, gameID);
        return makeRequest("PUT", "/game", request, JoinGameResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass)
            throws ServerFacadeException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ServerFacadeException(e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
