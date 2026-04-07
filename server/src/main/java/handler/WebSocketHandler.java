package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
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
