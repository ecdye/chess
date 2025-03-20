package websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.*;

import com.google.gson.Gson;

import ui.LiveMenu;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebsocketFacade extends Endpoint {
    private final URI socket;
    private final Session session;

    public WebsocketFacade(int port, LiveMenu menu) throws Exception {
        socket = new URI("ws://localhost:" + port + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socket);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage m = new Gson().fromJson(message, ServerMessage.class);
                switch (m.getServerMessageType()) {
                    case LOAD_GAME:
                        LoadGameMessage game = new Gson().fromJson(message, LoadGameMessage.class);
                        menu.updateGame(game.game);
                        break;

                    case NOTIFICATION:
                        NotificationMessage nm = new Gson().fromJson(message, NotificationMessage.class);
                        menu.displayNotification(nm.message);
                        break;

                    case ERROR:
                        ErrorMessage em = new Gson().fromJson(message, ErrorMessage.class);
                        menu.displayError(em.errorMessage);
                        break;
                }
            }
        });
    }

    public void connect(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        UserGameCommand command = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

	public void onOpen(Session session, EndpointConfig config) {}
}
