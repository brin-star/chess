package ServerFacade;

import chess.ChessGame;
import com.google.gson.Gson;
import result.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ServerFacade {
    private String serverURL = null;

    private record CreateGameBody(String gameName) {}

    public ServerFacade(int port) {
        serverURL = "http://localhost:" + port;
    }

    private <T> T makeRequest(String method, String endpoint, Object requestBody, String authToken, Class<T> responseClass) throws Exception {
        var url = new URL(serverURL + endpoint);
        var gson = new Gson();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            con.setRequestProperty("Authorization", authToken);
        }

        if (requestBody != null) {
            con.setDoOutput(true);

            String json = gson.toJson(requestBody);

            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            osw.write(json);
            osw.flush();
            osw.close();
        }

        con.connect();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            InputStream err = con.getErrorStream();
            InputStreamReader errISR = new InputStreamReader(err);
            var errMsg = gson.fromJson(errISR, Map.class);
            throw new Exception(errMsg.get("message").toString());
        }

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        T result = gson.fromJson(isr, responseClass);
        return result;
    }

    public ClearResult clear() throws Exception {
        return makeRequest("DELETE", "/db", null, null, ClearResult.class);
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", registerRequest, null, RegisterResult.class);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return makeRequest("POST", "/session", loginRequest, null, LoginResult.class);
    }

    public LogoutResult logout(String authToken) throws Exception {
        return makeRequest("DELETE", "/session", null, authToken, LogoutResult.class);
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception {
        CreateGameBody createGameBody = new CreateGameBody(gameName);
        return makeRequest("POST", "/game", createGameBody, authToken, CreateGameResult.class);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, authToken, ListGamesResult.class);
    }

    public JoinGameResult joinGame(String authToken, int gameID, ChessGame.TeamColor playerColor) throws Exception {
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, gameID, playerColor);
        return makeRequest("PUT", "/game", joinGameRequest, authToken, JoinGameResult.class);
    }
}
