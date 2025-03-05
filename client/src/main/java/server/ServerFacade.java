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
        return this.makeRequest(serverURL, "/db", null, ClearResult.class);
    }

    public RegisterResult register(String username, String password, String email) throws ServerFacadeException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResult result = this.makeRequest("POST", "/user", request, RegisterResult.class);
        authToken = result.authToken();
        return result;
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass)
            throws ServerFacadeException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

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
