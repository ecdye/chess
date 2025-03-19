package server.websocket;

import org.eclipse.jetty.websocket.api.annotations.*;

import com.google.gson.Gson;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jetty.websocket.api.*;

@WebSocket
public class WebsocketHandler {
    private final GameService gameService;
    private final AuthDAO authDAO;
    private HashMap<Integer, HashSet<Session>> allClients;

    public WebsocketHandler(GameService gameService, AuthDAO authDAO) {
        this.gameService = gameService;
        this.authDAO = authDAO;
        allClients = new HashMap<>();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        try {
            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnect(session, command);
                    break;

                case MAKE_MOVE:
                    MoveCommand move = new Gson().fromJson(message, MoveCommand.class);
                    gameService.makeMove(move.getGameID(), move.getMove());
                    moveNotification(move, session);
                    break;

                default:
                    break;
            }
        } catch (DataAccessException | IOException e) {
            // Send an error message over the WS
        }
    }

    private void broadcast(HashSet<Session> clients, ServerMessage message) throws IOException{
        for (Session s : clients) {
            s.getRemote().sendString(new Gson().toJson(message));
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws DataAccessException, IOException {
        HashSet<Session> clients = allClients.computeIfAbsent(command.getGameID(), k -> new HashSet<>());
        clients.add(session);
        GameData game = gameService.getGame(command.getGameID());

        LoadGameMessage message = new LoadGameMessage(LOAD_GAME, game.game());
        session.getRemote().sendString(new Gson().toJson(message));

        String username = authDAO.getAuth(command.getAuthToken()).username();
        String black = game.blackUsername();
        String white = game.whiteUsername();
        NotificationMessage m;
        if (username.equals(black)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as black");
        } else if (username.equals(white)) {
            m = new NotificationMessage(NOTIFICATION, username + " joined as white");
        } else {
            m = new NotificationMessage(NOTIFICATION, username + " joined as an observer");
        }
        broadcast(clients, m);
    }

    private void moveNotification(MoveCommand move, Session session) {
        // Broadcast
    }
}
