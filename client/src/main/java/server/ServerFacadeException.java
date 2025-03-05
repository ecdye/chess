package server;

/**
 * Indicates there was an error connecting to the database
 */
public class ServerFacadeException extends Exception {
    public ServerFacadeException(String message) {
        super(message);
    }
}
