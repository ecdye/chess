package server.websocket;

import org.eclipse.jetty.websocket.api.annotations.*;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import service.GameService;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

import org.eclipse.jetty.websocket.api.*;

@WebSocket
public class WebsocketHandler {
    private final GameService gameService;

    public WebsocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        try {
        switch (command.getCommandType()) {
            case MAKE_MOVE:
                MoveCommand move = new Gson().fromJson(message, MoveCommand.class);
                gameService.makeMove(move.getGameID(), move.getMove());
            default:
                break;
        }
        } catch (DataAccessException e) {
            // Send an error message over the WS
        }
    }
}
