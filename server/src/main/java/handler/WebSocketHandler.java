package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import server.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connectionManager;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, ConnectionManager connectionManager) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.connectionManager = connectionManager;
    }

    public void onMessage(WsMessageContext wsMessageContext) {
        var base = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);

        switch (base.getCommandType()) {
            case CONNECT -> handleConnect(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class), wsMessageContext);
            case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class));
            case LEAVE -> handleLeave(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class));
            case RESIGN -> handleResign(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class));
        }

    }

    public void onConnect(WsConnectContext wsConnectContext) {
    }

    public void onClose(WsCloseContext wsCloseContext) {
        connectionManager.remove(wsCloseContext.session);
    }

    private void handleConnect(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
        try {
            AuthData authData = authDAO.getAuth(userGameCommand.getAuthToken());
            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

            if (authData == null) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: unauthorized"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (gameData == null ) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: no game found"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            connectionManager.add(userGameCommand.getGameID(), wsMessageContext.session);

            String messageToSend = new Gson().toJson(new LoadGameMessage(gameData));
            connectionManager.sendToOne(wsMessageContext.session, messageToSend);

            String notification;

            if (authData.username().equals(gameData.whiteUsername())) {
                notification = gameData.whiteUsername() + " joined as WHITE";
            }
            else if (authData.username().equals(gameData.blackUsername())) {
                notification = gameData.blackUsername() + " joined as BLACK";
            }
            else {
                notification = authData.username() + " joined as observer";
            }

            messageToSend = new Gson().toJson(new NotificationMessage(notification));
            connectionManager.broadcastToAllExcept(userGameCommand.getGameID(), wsMessageContext.session, messageToSend);
        }
        catch (Exception e) {
            String errorJson = new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
            try {
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand) {

    }

    private void handleLeave(UserGameCommand userGameCommand) {

    }

    private void handleResign(UserGameCommand userGameCommand) {

    }
}
