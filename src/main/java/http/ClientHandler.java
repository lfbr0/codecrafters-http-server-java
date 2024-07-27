package http;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
            this.socket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
            this.socket.close();
        }
        catch (Exception ex) {
            instanceLogger.error("Failed to handle HTTP client", ex);
        }
    }

}
