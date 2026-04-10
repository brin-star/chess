package websocket;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void receiveMessage(ServerMessage message);
}
