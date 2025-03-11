package ui;

import static ui.PreLoginMenu.printCommand;
import static ui.PreLoginMenu.printError;

import java.util.Scanner;

import client.ChessClient;
import model.GameData;
import model.results.ListGamesResult;

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
            ListGamesResult result = client.server.listGames();
            if (result.message() != null) {
                printError(result.message());
                return;
            }
            GameData[] games = result.games().toArray(new GameData[1]);

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

}
