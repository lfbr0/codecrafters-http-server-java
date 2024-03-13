package handler;

import log.ApplicationLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ClientHandler implements Runnable, Closeable {

    private static final int CLIENT_INPUT_BUFFER_SIZE = 16000;
    private static final int CLIENT_OUTPUT_BUFFER_SIZE = 16000;
    private final ApplicationLogger logger;
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.logger = ApplicationLogger.getPrototypeInstance(
                ClientHandler.class,
                clientSocket.getRemoteSocketAddress().toString()
        );
    }

    @Override
    public void run() {
        try {
            final InputStream is = clientSocket.getInputStream();
            final OutputStream os = clientSocket.getOutputStream();
            os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("Failed to handle client", e);
        }
    }

    @Override
    public void close() throws IOException {
        logger.info("Closing down client handler!");
        if (!clientSocket.isClosed()) {
            clientSocket.close();
        }
    }

}
