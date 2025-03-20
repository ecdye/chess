package ui;

import static ui.PreLoginMenu.printCommand;
import static ui.PostLoginMenu.printChessBoard;
import static ui.PreLoginMenu.printError;

import java.util.Scanner;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ChessClient;
import websocket.WebsocketFacade;

public class LiveMenu {
    private final ChessClient client;
    private final Scanner s;
    private final boolean black;
    private final WebsocketFacade socket;
    private ChessGame currentGame = new ChessGame();
    private int gameID;

    public LiveMenu(ChessClient client, Scanner s, boolean black) throws Exception {
        this.client = client;
        this.s = s;
        this.black = black;
        socket = new WebsocketFacade(client.port, this);
    }

    public void run(int gameID) throws Exception {
        this.gameID = gameID;
        this.socket.connect(client.server.authToken, gameID);
        String input = "";
        while (true) {
            System.out.printf("\n[GAME] >>> ");
            input = s.nextLine();
            if (input.equals("leave")) {
                this.socket.leaveGame(client.server.authToken, gameID);
                break;
            }
            eval(input.split(" "));
        }
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                printCommand("redraw", "the chess board");
                printCommand("move <BEGIN> <END>", "a piece");
                printCommand("resign", "from a game");
                printCommand("highlight <PIECE>", "possible moves");
                printCommand("help", "print this message");
                break;

            case "redraw":
                updateGame();
                break;

            case "move":
                if (input.length != 3) {
                    printError("invalid arguments");
                    printCommand("move <BEGIN> <END>", "a piece");
                    break;
                }
                interpretMove(input[1], input[2]);
                break;

            case "resign":
                // this.socket.resignGame(client.server.authToken);
                break;

            default:
                printError("unknown command: " + input[0]);
                break;
        }
    }

    public void updateGame(ChessGame game) {
        currentGame = game;
        updateGame();
    }

    public void updateGame() {
        printChessBoard(currentGame, black);
        System.out.printf("\n[GAME] >>> ");
    }

    public void displayNotification(String n) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_MAGENTA);
        System.out.println(n + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    public void displayError(String e) {
        System.out.print(EscapeSequences.ERASE_LINE + EscapeSequences.SET_BG_COLOR_MAGENTA);
        printError(e + EscapeSequences.RESET_BG_COLOR);
        System.out.printf("\n[GAME] >>> ");
    }

    private void interpretMove(String from, String to) {
        if (from.length() != 2 || to.length() != 2) {
            printError("invalid positions");
            return;
        }

        char fromCol = from.charAt(0);
        char toCol = to.charAt(0);
        int fromRow = Character.getNumericValue(from.charAt(1));
        int toRow = Character.getNumericValue(to.charAt(1));

        if (fromCol < 'a' || fromCol > 'h' || toCol < 'a' || toCol > 'h' || fromRow < 1 || fromRow > 8 || toRow < 1 || toRow > 8) {
            printError("positions out of bounds");
            return;
        }

        ChessPosition start = new ChessPosition(fromRow, fromCol - 'a' + 1);
        ChessPosition end = new ChessPosition(toRow, toCol - 'a' + 1);
        ChessMove move = new ChessMove(start, end, null);

        try {
            this.socket.makeMove(client.server.authToken, gameID, move);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }
}
