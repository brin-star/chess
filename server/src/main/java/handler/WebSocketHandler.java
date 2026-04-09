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

    private void handleMakeMove(MakeMoveCommand makeMoveCommand, WsMessageContext wsMessageContext) {
        try {
            AuthData authData = authDAO.getAuth(makeMoveCommand.getAuthToken());
            GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());

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

            if (gameData.game().isGameOver()) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: game is already over"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (!authData.username().equals(gameData.whiteUsername()) && !authData.username().equals(gameData.blackUsername())) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: observers can't make moves"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (authData.username().equals(gameData.whiteUsername()) && gameData.game().getTeamTurn() != ChessGame.TeamColor.WHITE
                    || authData.username().equals(gameData.blackUsername()) && gameData.game().getTeamTurn() != ChessGame.TeamColor.BLACK) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: not " + authData.username() + "'s turn"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            try {
                gameData.game().makeMove(makeMoveCommand.getMove());
            } catch (InvalidMoveException e) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            String messageToSend = new Gson().toJson(new LoadGameMessage(gameData));
            connectionManager.broadcastToAll(gameData.gameID(), messageToSend);

            messageToSend = new Gson().toJson(new NotificationMessage(authData.username() + " moved " + makeMoveCommand.getMove().toString()));
            connectionManager.broadcastToAllExcept(gameData.gameID(), wsMessageContext.session, messageToSend);

            String opponentUsername;

            if (gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
                opponentUsername = gameData.whiteUsername();
            }
            else {
                opponentUsername = gameData.blackUsername();
            }

            if (gameData.game().isInCheckmate(gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage(opponentUsername + " is in checkmate"));
                connectionManager.broadcastToAll(gameData.gameID(), messageToSend);
                gameData.game().setGameOver(true);
            }
            else if (gameData.game().isInStalemate(gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage("Stalemate"));
                connectionManager.broadcastToAll(gameData.gameID(), messageToSend);
                gameData.game().setGameOver(true);
            }
            else if (gameData.game().isInCheck(gameData.game().getTeamTurn())) {
                messageToSend = new Gson().toJson(new NotificationMessage(opponentUsername + " is in check"));
                connectionManager.broadcastToAll(gameData.gameID(), messageToSend);
            }

            gameDAO.updateGameInDB(gameData);
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

    private void handleLeave(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {

    }

    private void handleResign(UserGameCommand userGameCommand, WsMessageContext wsMessageContext) {
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

            if (gameData.game().isGameOver()) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: game is already over"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            if (!authData.username().equals(gameData.whiteUsername()) && !authData.username().equals(gameData.blackUsername())) {
                String errorJson = new Gson().toJson(new ErrorMessage("Error: observers can't resign"));
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
                return;
            }

            gameData.game().setGameOver(true);
            gameDAO.updateGameInDB(gameData);

            String messageToSend = new Gson().toJson(new NotificationMessage(authData.username() + " resigned from game: " + gameData.gameName()));
            connectionManager.broadcastToAll(gameData.gameID(), messageToSend);
        }
        catch (DataAccessException e) {
            String errorJson = new Gson().toJson(new ErrorMessage("Error: " + e.getMessage()));
            try {
                connectionManager.sendToOne(wsMessageContext.session, errorJson);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
