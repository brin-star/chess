package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import websocket.ConnectionManager;
import websocket.commands.UserGameCommand;

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
            case CONNECT -> handleConnect(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class));
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

    private void handleConnect(UserGameCommand userGameCommand) {
        connectionManager.add(base, wsMessageContext.session);
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand) {

    }

    private void handleLeave(UserGameCommand userGameCommand) {

    }

    private void handleResign(UserGameCommand userGameCommand) {

    }
}
