package http;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final ApplicationLogger instanceLogger;
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.instanceLogger = ApplicationLoggerFactory.getLogger(
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
        );
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            this.socket.close();
        }
        catch (Exception ex) {
            instanceLogger.error("Failed to handle HTTP client", ex);
        }
    }

}
