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
        getRequest()
                .flatMap(req -> getResponse(req))
                .ifPresent(resp -> sendResponse(resp));
    }

    private Optional<ByteArrayOutputStream> getRequest() {
        try {
            logger.info("Will start to listen to request...");
            InputStream inputStream = clientSocket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] input = new byte[CLIENT_INPUT_BUFFER_SIZE];

            for (int bytesRead = inputStream.read(input); bytesRead != -1; bytesRead = inputStream.read(input)) {
                buffer.write(input, 0, bytesRead);
            }

            return Optional.of(buffer);
        } catch (IOException e) {
            logger.error("Error while listening to client request", e);
            return Optional.empty();
        }
    }

    private Optional<StringBuffer> getResponse(ByteArrayOutputStream requestBuffer) {
        logger.info("Received request:\n$$START$$\n" + requestBuffer + "\n$$END$$\n");
        StringBuffer responseBuffer = new StringBuffer(CLIENT_OUTPUT_BUFFER_SIZE);
        try {
            //TODO actual work here
            responseBuffer.append("HTTP/1.1 200 OK\r\n\r\n");

            //Return response buffer
            return Optional.of(responseBuffer);
        } catch (Exception e) {
            logger.error("Failed to interpret client request and open client output stream", e);
            return Optional.empty();
        }
    }

    private void sendResponse(StringBuffer responseBuffer) {
        try (OutputStream outputStream = clientSocket.getOutputStream()) {
            logger.info("Sending response to client -> " + responseBuffer);
            outputStream.write(responseBuffer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Failed to open client output stream and send response", e);
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
