package handler;

import http.HttpRequest;
import http.HttpUtils;
import log.ApplicationLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ClientHandler extends Thread {

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
        logger.info("Got an incoming connection from " + this.clientSocket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            logger.info("Proceeding to listen to request");
            StringBuffer request = getRequestBuffer();

            logger.info("Proceeding to interpret request");
            StringBuffer response = getResponseBuffer(request);

            logger.info("Proceeding with sending response");
            sendResponse(response);
        } catch (Exception e) {
            logger.error("Failed to handle client", e);
        } finally {
            logger.info("Closing down client handler!");
        }
    }


    private StringBuffer getRequestBuffer() {
        StringBuffer requestBuffer = new StringBuffer(CLIENT_INPUT_BUFFER_SIZE);

        InputStreamReader clientInputStreamReader = null;
        try {
            clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to open client input stream reader", e);
            return requestBuffer;
        }

        BufferedReader bufferedReader = new BufferedReader(clientInputStreamReader);
        try {
            for (
                    String line = bufferedReader.readLine();
                    line != null && !line.equals(HttpUtils.HTTP_DELIMITER) && !line.isEmpty();
                    line = bufferedReader.readLine()
            ) {
                requestBuffer
                        .append(line)
                        .append(HttpUtils.HTTP_NEW_LINE);
            }
        } catch (IOException e) {
            logger.error("Failed to read input stream from client", e);
            return requestBuffer;
        }

        return requestBuffer;
    }

    private StringBuffer getResponseBuffer(StringBuffer requestBuffer) {
        StringBuffer responseBuffer = new StringBuffer(CLIENT_OUTPUT_BUFFER_SIZE);

        if (requestBuffer != null) {
            HttpRequest request = new HttpRequest(requestBuffer);

            if (request.getDesiredPath().equals("/")) {
                responseBuffer.append("HTTP/1.1 200 OK");
            }
            else if (request.getDesiredPath().startsWith("/echo")) {

                String echoString = request
                        .getDesiredPath()
                        .substring("/echo".length()+1);

                responseBuffer
                        .append("HTTP/1.1 200 OK")
                        .append(HttpUtils.HTTP_NEW_LINE)
                        .append("Content-Type: text/plain")
                        .append(HttpUtils.HTTP_NEW_LINE)
                        .append("Content-Length: ").append(echoString.length())
                        .append(HttpUtils.HTTP_NEW_LINE).append(HttpUtils.HTTP_NEW_LINE)
                        .append(echoString);
            } else {
                responseBuffer.append("HTTP/1.1 404 Not Found");
            }

        }
        
        responseBuffer.append(HttpUtils.HTTP_DELIMITER);
        return responseBuffer;
    }

    private void sendResponse(StringBuffer response) {
        try (OutputStream outputStream = clientSocket.getOutputStream()){
            if (response != null) {
                String responseContent = response.toString();
                logger.info("RESPONSE_START$$$\n" + responseContent + "\n$$$RESPONSE_END\n");
                outputStream.write(responseContent.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            logger.error("Failed to send response to client!", e);
        }
    }

}
