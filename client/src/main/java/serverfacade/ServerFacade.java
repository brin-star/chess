package serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.DeploymentException;
import result.*;
import websocket.ServerMessageObserver;
import websocket.WebsocketCommunicator;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class ServerFacade {
    private String serverURL = null;
    private ServerMessageObserver observer;
    private WebsocketCommunicator ws = null;

    private record CreateGameBody(String gameName) {}

    public ServerFacade(int port, ServerMessageObserver observer) {
        serverURL = "http://localhost:" + port;
        this.observer = observer;
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

    // Websocket functions
    public void connectToGame(String authToken, int gameID) throws IOException, DeploymentException, URISyntaxException {
        String wsURL = serverURL.replace("http", "ws");
        this.ws = new WebsocketCommunicator(wsURL, observer);
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        ws.send(new Gson().toJson(cmd));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        MakeMoveCommand cmd = new MakeMoveCommand(authToken, gameID, move);
        ws.send(new Gson().toJson(cmd));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        ws.send(new Gson().toJson(cmd));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        ws.send(new Gson().toJson(cmd));
    }

    public void setObserver(ServerMessageObserver observer) {
        this.observer = observer;
    }
}
