package client;

import server.ServerFacade;

public class ChessClient {
    public final int port;
    public final ServerFacade server;

    public ChessClient(int port) {
        this.port = port;
        server = new ServerFacade(port);
    }
}
