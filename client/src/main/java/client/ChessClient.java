package client;

import server.ServerFacade;

public class ChessClient {
    public final ServerFacade server;

    public ChessClient(int port) {
        server = new ServerFacade(port);
    }
}
