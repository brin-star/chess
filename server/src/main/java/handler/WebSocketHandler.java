package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.ConnectionManager;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connectionManager;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, ConnectionManager connectionManager) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.connectionManager = connectionManager;
    }

    record AuthGameBundle(AuthData authData, GameData gameData) {};

    public void onMessage(WsMessageContext wsMessageContext) {
        var base = new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class);

        switch (base.getCommandType()) {
            case CONNECT -> handleConnect(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class), wsMessageContext);
            case MAKE_MOVE -> handleMakeMove(new Gson().fromJson(wsMessageContext.message(), MakeMoveCommand.class), wsMessageContext);
            case LEAVE -> handleLeave(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class), wsMessageContext);
            case RESIGN -> handleResign(new Gson().fromJson(wsMessageContext.message(), UserGameCommand.class), wsMessageContext);
        }

    }

    public void onConnect(WsConnectContext wsConnectContext) {
    }

    public void onClose(WsCloseContext wsCloseContext) {
        connectionManager.remove(wsCloseContext.session);
    }

    private void handleConnect(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
        try {
            AuthGameBundle bundle = validate(userGameCommand, wsMessageContext);
            if (bundle == null) {
                return;
            }

            connectionManager.add(userGameCommand.getGameID(), wsMessageContext.session);

            String messageToSend = new Gson().toJson(new LoadGameMessage(bundle.gameData));
            connectionManager.sendToOne(wsMessageContext.session, messageToSend);

            String notification;

            if (bundle.authData.username().equals(bundle.gameData.whiteUsername())) {
                notification = bundle.gameData.whiteUsername() + " joined as WHITE";
            }
            else if (bundle.authData.username().equals(bundle.gameData.blackUsername())) {
                notification = bundle.gameData.blackUsername() + " joined as BLACK";
            }
            else {
                notification = bundle.authData.username() + " joined as observer";
            }

            messageToSend = new Gson().toJson(new NotificationMessage(notification));
            connectionManager.broadcastToAllExcept(userGameCommand.getGameID(), wsMessageContext.session, messageToSend);
        }
        catch (Exception e) {
            sendError(wsMessageContext.session, e);
        }
    }

    private void handleMakeMove(MakeMoveCommand makeMoveCommand, WsMessageContext wsMessageContext) {
        try {
            AuthGameBundle bundle = validate(makeMoveCommand, wsMessageContext);
            if (bundle == null) {
                return;
            }

            if (bundle.gameData.game().isGameOver()) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: game is already over"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (!bundle.authData.username().equals(bundle.gameData.whiteUsername())
                    && !bundle.authData.username().equals(bundle.gameData.blackUsername())) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: observers can't make moves"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (bundle.authData.username().equals(bundle.gameData.whiteUsername())
                    && bundle.gameData.game().getTeamTurn() != ChessGame.TeamColor.WHITE
                    || bundle.authData.username().equals(bundle.gameData.blackUsername())
                    && bundle.gameData.game().getTeamTurn() != ChessGame.TeamColor.BLACK) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: not " + bundle.authData.username() + "'s turn"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            try {
                bundle.gameData.game().makeMove(makeMoveCommand.getMove());
            } catch (InvalidMoveException e) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            String messageToSend = new Gson().toJson(new LoadGameMessage(bundle.gameData));
            connectionManager.broadcastToAll(bundle.gameData.gameID(), messageToSend);

            messageToSend = new Gson().toJson(new NotificationMessage(bundle.authData.username() + " moved " + makeMoveCommand.getMove().toString()));
            connectionManager.broadcastToAllExcept(bundle.gameData.gameID(), wsMessageContext.session, messageToSend);

            String opponentUsername;

            if (bundle.gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
                opponentUsername = bundle.gameData.whiteUsername();
            }
            else {
                opponentUsername = bundle.gameData.blackUsername();
            }

            if (bundle.gameData.game().isInCheckmate(bundle.gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage(opponentUsername + " is in checkmate"));
                connectionManager.broadcastToAll(bundle.gameData.gameID(), messageToSend);
                bundle.gameData.game().setGameOver(true);
            }
            else if (bundle.gameData.game().isInStalemate(bundle.gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage("Stalemate"));
                connectionManager.broadcastToAll(bundle.gameData.gameID(), messageToSend);
                bundle.gameData.game().setGameOver(true);
            }
            else if (bundle.gameData.game().isInCheck(bundle.gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage(opponentUsername + " is in check"));
                connectionManager.broadcastToAll(bundle.gameData.gameID(), messageToSend);
            }

            gameDAO.updateGameInDB(bundle.gameData);
        }
        catch (Exception e) {
            sendError(wsMessageContext.session, e);
        }
    }

    private void handleLeave(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
        try {
            AuthGameBundle bundle = validate(userGameCommand, wsMessageContext);
            if (bundle == null) {
                return;
            }

            if (bundle.authData.username().equals(bundle.gameData.whiteUsername())) {
                gameDAO.updateGameInDB(
                        new GameData(bundle.gameData.gameID(), null,
                                bundle.gameData.blackUsername(), bundle.gameData.gameName(), bundle.gameData.game())
                );
            }
            else if (bundle.authData.username().equals(bundle.gameData.blackUsername())) {
                gameDAO.updateGameInDB(
                        new GameData(bundle.gameData.gameID(), bundle.gameData.whiteUsername(), null,
                                bundle.gameData.gameName(), bundle.gameData.game())
                );
            }

            connectionManager.remove(wsMessageContext.session);

            String messageToSend = new Gson().toJson(new NotificationMessage(bundle.authData.username() + " left the game"));
            connectionManager.broadcastToAllExcept(userGameCommand.getGameID(), wsMessageContext.session, messageToSend);

        }
        catch (DataAccessException e) {
            sendError(wsMessageContext.session, e);
        }
    }

    private void handleResign(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
        try {
            AuthGameBundle bundle = validate(userGameCommand, wsMessageContext);
            if (bundle == null) {
                return;
            }

            if (bundle.gameData.game().isGameOver()) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: game is already over"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (!bundle.authData.username().equals(bundle.gameData.whiteUsername())
                    && !bundle.authData.username().equals(bundle.gameData.blackUsername())) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: observers can't resign"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            bundle.gameData.game().setGameOver(true);
            gameDAO.updateGameInDB(bundle.gameData);

            String messageToSend = new Gson().toJson(
                    new NotificationMessage(bundle.authData.username() + " resigned from game: " + bundle.gameData.gameName())
            );
            connectionManager.broadcastToAll(bundle.gameData.gameID(), messageToSend);
        }
        catch (DataAccessException e) {
            sendError(wsMessageContext.session, e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendError(Session session, Exception message) {
        String errorJson = new Gson().toJson(new ErrorMessage("Error: " + message));
        try {
            connectionManager.sendToOne(session, errorJson);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private AuthGameBundle validate(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
        try {
            AuthData authData = authDAO.getAuth(userGameCommand.getAuthToken());
            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

            if (authData == null) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: unauthorized"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return null;
            }

            if (gameData == null) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: no game found"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return null;
            }

            return new AuthGameBundle(authData, gameData);
        } catch (Exception e) {
            sendError(wsMessageContext.session, e);
        }
        return null;
    }
}
