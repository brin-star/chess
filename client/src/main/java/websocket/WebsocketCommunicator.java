package websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    private final Session session;
    private final ServerMessageObserver observer;

    public WebsocketCommunicator(String serverUrl, ServerMessageObserver observer) throws URISyntaxException, DeploymentException, IOException {
        this.observer = observer;
        URI uri = new URI(serverUrl + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                observer.receiveMessage(serverMessage);
            }
        });
    }

    public void send(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }
}
