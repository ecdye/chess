package ui;

import static ui.PreLoginMenu.printCommand;
import static ui.PreLoginMenu.printError;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessGame.TeamColor;
import client.ChessClient;
import model.GameData;
import model.results.JoinGameResult;
import model.results.ListGamesResult;
import server.ServerFacadeException;

public class PostLoginMenu {
    private ChessClient client;
    private final String username;
    private final Scanner s;

    public PostLoginMenu(ChessClient client, String username, Scanner s) {
        this.client = client;
        this.username = username;
        this.s = s;
        System.out.println("Logged in as " + username);
    }

    public void run() {
        String input = "";
        while (true) {
            System.out.printf("\n[LOGGED IN] >>> ");
            input = s.nextLine();
            if (input.equals("logout")) {
                System.out.println("Logged out " + username);
                break;
            }
            eval(input.split(" "));
        }
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                printCommand("create <NAME>", "a game");
                printCommand("list", "games");
                printCommand("join <ID> <WHITE|BLACK>", "a game");
                printCommand("observe <ID>", "a game");
                printCommand("logout", "when you are done");
                printCommand("help", "print this message");
                break;

            case "create":
                if (input.length != 2) {
                    printError("invalid number of arguments");
                    printCommand("create <NAME>", "a game");
                    break;
                }
                handleCreateGame(input[1]);
                break;

            case "list":
                handleListGames();
                break;

            case "join":
                if (input.length != 3 && (input[2] != "WHITE" || input[2] != "BLACK")) {
                    printError("invalid arguments");
                    printCommand("join <ID> <WHITE|BLACK>", "a game");
                }
                handleJoin(input[1], input[2]);
                break;

            case "observe":
                if (input.length != 2) {
                    printError("invalid number of arguments");
                    printCommand("observe <ID>", "a game");
                }
                handleObserve(input[1]);
                break;

            case "logout":
                // Logout handled in run method
                break;

            default:
                printError("unknown command: " + input);
                break;
        }
    }

    private void handleCreateGame(String name) {
        try {
            client.server.createGame(name);
            System.out.println("Game has been successfully created!");
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void handleListGames() {
        try {
            GameData[] games = gamesList();

            for (int i = 0; i < games.length; ++i) {
                boolean player = false;
                System.out.println(i + ". " + games[i].gameName() + ":");
                if (games[i].whiteUsername() != null) {
                    System.out.println("    White: " + games[i].whiteUsername());
                    player = true;
                }
                if (games[i].blackUsername() != null) {
                    System.out.println("    Black: " + games[i].blackUsername());
                    player = true;
                }
                if (!player) {
                    System.out.println("    No players!");
                }
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void handleJoin(String name, String color) {
        try {
            GameData gameData = selectGameByName(name);
            if (gameData == null) {
                printError("Game not found");
                return;
            }

            JoinGameResult result = client.server.joinGame(color, gameData.gameID());
            if (result.message() != null) {
                printError(result.message());
                return;
            }

            printChessBoard(gameData.game(), color.equals("BLACK"));
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private void handleObserve(String name) {
        try {
            GameData gameData = selectGameByName(name);
            if (gameData == null) {
                printError("Game not found");
                return;
            }

            printChessBoard(gameData.game(), false);
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    private GameData selectGameByName(String name) throws ServerFacadeException {
        GameData[] games = gamesList();
        for (GameData game : games) {
            if (game.gameName().equals(name)) {
                return game;
            }
        }
        return null;
    }

    private GameData[] gamesList() throws ServerFacadeException {
        ListGamesResult result = client.server.listGames();
        if (result.message() != null) {
            printError(result.message());
            return new GameData[0];
        }
        return result.games().toArray(new GameData[0]);
    }


    private void printChessBoard(ChessGame game, boolean blackView) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(EscapeSequences.ERASE_SCREEN);
        drawLetters(out, blackView);
        drawBoard(out, blackView, game.getBoard());
        drawLetters(out, blackView);
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void drawLetters(PrintStream out, boolean blackView) {
        ArrayList<String> columns = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        if (blackView) {
            columns = new ArrayList<>(columns.reversed());
        }

        out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        out.print(EscapeSequences.EMPTY);
        for (var col : columns) {
            out.print(" " + col + " ");
        }
        out.println(EscapeSequences.EMPTY);
    }

    private void drawBoard(PrintStream out, boolean blackView, ChessBoard board) {
        ArrayList<String> rows = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8"));
        if (blackView) {
            rows = new ArrayList<>(rows.reversed());
        }
        for (int r = 0; r < 8; ++r) {
            out.print(" " + rows.get(r) + " ");
            for (int c = 0; c < 8; ++c) {
                if ((r + c) % 2 == 0) {
                    out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                }
                int properRow = blackView ? 8 - r : r + 1;
                ChessPiece piece = board.getPiece(new ChessPosition(properRow, c + 1));
                if (piece != null) {
                    out.print(getPieceEscapeSequence(piece));
                } else {
                    out.print(EscapeSequences.EMPTY);
                }
            }
            out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + " " + rows.get(r) + " ");
        }
    }

    private String getPieceEscapeSequence(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN:
                return piece.getTeamColor() == TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }

}
