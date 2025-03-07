import ui.PreLoginMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to the 240 Chess Client! Type 'help' to get started. ♕");
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        new PreLoginMenu(port).run();
    }
}
