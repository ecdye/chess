package ui;

import static ui.PreLoginMenu.printCommand;
import static ui.PostLoginMenu.printChessBoard;
import static ui.PreLoginMenu.printError;

import java.util.Collection;
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
                renderGame(null, false);
                break;

            case "move":
                if (input.length != 3) {
                    printError("invalid arguments");
                    printCommand("move <BEGIN> <END>", "a piece");
                    break;
                }
                interpretMove(input[1], input[2]);
                break;

            case "highlight":
                if (input.length != 2) {
                    printError("invalid arguments");
                    printCommand("highlight <PIECE>", "possible moves");
                    break;
                }
                handleHighlight(input[1]);
                break;

            case "resign":
                handleResign();
                break;

            default:
                printError("unknown command: " + input[0]);
                break;
        }
    }

    public void updateGame(ChessGame game) {
        currentGame = game;
        renderGame(null, true);
    }

    public void renderGame(Collection<ChessMove> validMoves, boolean print) {
        printChessBoard(currentGame, black, validMoves);
        if (print) {
            System.out.printf("\n[GAME] >>> ");
        }
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

    private void handleHighlight(String pos) {
        renderGame(currentGame.validMoves(parsePosition(pos)), false);
    }

    private void handleResign() {
        try {
            System.out.print("Please confirm: (Y)es (N)o >>> ");
            String choice = s.nextLine();
            if (choice.charAt(0) == 'Y') {
                socket.resignGame(client.server.authToken, gameID);
            } else {
                System.out.println("OK, cancelling");
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void interpretMove(String from, String to) {
        try {
            ChessPosition start = parsePosition(from);
            ChessPosition end = parsePosition(to);
            ChessMove move = new ChessMove(start, end, null);

            this.socket.makeMove(client.server.authToken, gameID, move);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private ChessPosition parsePosition(String coordinate) {
        if (coordinate.length() != 2) {
            throw new IllegalArgumentException("invalid position format");
        }

        char col = coordinate.charAt(0);
        int row = Character.getNumericValue(coordinate.charAt(1));

        if (col < 'a' || col > 'h' || row < 1 || row > 8) {
            throw new IllegalArgumentException("position out of bounds");
        }

        return new ChessPosition(row, col - 'a' + 1);
    }
}
