package ui;

import java.util.Scanner;

import client.ChessClient;
import model.results.LoginResult;
import model.results.RegisterResult;
import server.ServerFacadeException;

public class PreLoginMenu {
    private ChessClient client;
    private final Scanner s;

    public PreLoginMenu(int port) {
        client = new ChessClient(port);
        s = new Scanner(System.in);
    }

    public void run() {
        String input = "";
        while (!input.equals("quit")) {
            System.out.printf("\n[LOGGED OUT] >>> ");
            try {
                input = s.nextLine();
                if (input.equals("quit")) {
                    System.out.println("Bye!");
                    break;
                }
                eval(input.split(" "));
            } catch (Exception e) {
                printError(e.getMessage());
            }
        }
        s.close();
    }

    private void eval(String[] input) {
        switch (input[0].toLowerCase()) {
            case "help":
                printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account");
                printCommand("login <USERNAME> <PASSWORD>", "to play a game");
                printCommand("quit", "to stop playing chess");
                printCommand("help", "display this help message");
                break;

            case "quit":
                System.out.println("Bye!");
                break;

            case "register":
                if (input.length != 4) {
                    printError("invalid number of arguments");
                    printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account");
                    break;
                }
                handleRegister(input);
                break;

            case "login":
                if (input.length != 3) {
                    printError("invalid number of arguments");
                    printCommand("login <USERNAME> <PASSWORD>", "to play a game");
                    break;
                }
                handleLogin(input);
                break;

            default:
                printError("unknown command: " + input[0]);
                break;
        }
    }

    private void handleRegister(String[] args) {
        try {
            RegisterResult result = client.server.register(args[1], args[2], args[3]);
            if (result.message() != null) {
                printError(result.message());
                return;
            }
            handlePostLogin(result.username());
        } catch (ServerFacadeException e) {
            printError(e.getMessage());
        }
    }

    private void handleLogin(String[] args) {
        try {
            LoginResult result = client.server.login(args[1], args[2]);
            if (result.message() != null) {
                printError(result.message());
                return;
            }
            handlePostLogin(result.username());
        } catch (ServerFacadeException e) {
            printError(e.getMessage());
        }
    }

    private void handlePostLogin(String username) {
        boolean exit = new PostLoginMenu(client, username, s).run();
        if (exit) {
            System.out.println("Bye!");
            s.close();
            System.exit(0);
        }
        System.out.println("Welcome back to the main menu!");
    }

    static void printCommand(String usage, String desc) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + usage + EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(" - ");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + desc + EscapeSequences.RESET_TEXT_COLOR);
    }

    static void printError(String message) {
        if (message.startsWith("Error: ")) {
            message = message.substring(7);
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + EscapeSequences.RESET_TEXT_COLOR + message);
    }
}
