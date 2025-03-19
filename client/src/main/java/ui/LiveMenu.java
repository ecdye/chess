package ui;

import static ui.PostLoginMenu.printChessBoard;

import java.util.Scanner;

import chess.ChessGame;
import client.ChessClient;
import websocket.WebsocketFacade;

public class LiveMenu {
    private final ChessClient client;
    private final Scanner s;
    private final boolean black;
    private final WebsocketFacade socket;

    public LiveMenu(ChessClient client, Scanner s, boolean black) throws Exception {
        this.client = client;
        this.s = s;
        this.black = black;
        socket = new WebsocketFacade(client.port, this);
    }

    public void run(int gameID) throws Exception {
        this.socket.connect(client.server.authToken, gameID);
        String input = "";
        while (true) {
            System.out.printf("\n[GAME] >>> ");
            input = s.nextLine();
            if (input.equals("leave")) {
                break;
            }
        }
    }

    public void updateGame(ChessGame game) {
        printChessBoard(game, black);
        System.out.printf("\n[GAME] >>> ");
    }
}
