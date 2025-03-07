package ui;

import java.util.Scanner;

import client.ChessClient;
import model.results.LoginResult;
import model.results.RegisterResult;
import server.ServerFacadeException;

public class PreLoginMenu {
    private ChessClient client;

    public PreLoginMenu(int port) {
        client = new ChessClient(port);
    }

    public void run() {
        Scanner s = new Scanner(System.in);
        String input = "";
        while (!input.equals("exit")) {
            System.out.printf("\n[LOGGED OUT] >>> ");
            input = s.nextLine();
            eval(input.split(" "));
        }
        s.close();
    }

    private void eval(String[] input) {

        switch (input[0].toLowerCase()) {
            case "help":
                printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account");
                printCommand("login <USERNAME> <PASSWORD>", "to play a game");
                printCommand("exit", "to stop playing chess");
                printCommand("help", "display this help message");
                break;

            case "exit":
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
                printError("unknown command: " + input);
                break;
        }
    }

    private void handleRegister(String[] args) {
        try {
            RegisterResult result = client.server.register(args[1], args[2], args[3]);
            // Now activate logged in mode
        } catch (ServerFacadeException e) {
            printError(e.getMessage());
        }
    }

    private void handleLogin(String[] args) {
        try {
            LoginResult result = client.server.login(args[1], args[2]);
            // Now activate logged in mode
        } catch (ServerFacadeException e) {
            printError(e.getMessage());
        }
    }

    private void printCommand(String usage, String desc) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + usage + EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(" - ");
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + desc + EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printError(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Error: " + EscapeSequences.RESET_TEXT_COLOR + message);
    }
}
