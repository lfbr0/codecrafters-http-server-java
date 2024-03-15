package handler;

import filesystem.FileSystemUtils;
import http.HttpRequest;
import http.HttpUtils;
import log.ApplicationLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {

    private static final int CLIENT_INPUT_BUFFER_SIZE = 16000;
    private static final int CLIENT_OUTPUT_BUFFER_SIZE = 16000;
    private final ApplicationLogger logger;
    private final Socket clientSocket;
    private final String directory;

    public ClientHandler(Socket clientSocket, String directory) {
        this.directory = directory;
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

        InputStreamReader clientInputStreamReader;
        try {
            clientInputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to open client input stream reader", e);
            return requestBuffer;
        }
        //TODO: add timeout on listen
        BufferedReader bufferedReader = new BufferedReader(clientInputStreamReader);
        try {
            int contentLength = 0;

            for (String line = bufferedReader.readLine(); line != null && !line.isEmpty(); line = bufferedReader.readLine()) {
                requestBuffer.append(line).append(HttpUtils.HTTP_NEW_LINE);

                if (line.contains("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            //if it has request body, also append it
            if (contentLength > 0) {
                char[] contentBody = new char[contentLength];
                int read = bufferedReader.read(contentBody, 0, contentLength);
                logger.info("Read " + read + " bytes from request body...");
                requestBuffer.append(contentBody);
            }
        } catch (IOException ex) {
            logger.error("Failed to read content", ex);
            return requestBuffer;
        }

        return requestBuffer;
    }


    private StringBuffer getResponseBuffer(StringBuffer requestBuffer) {
        StringBuffer responseBuffer = new StringBuffer(CLIENT_OUTPUT_BUFFER_SIZE);

        if (requestBuffer != null && requestBuffer.length() > 0) {
            HttpRequest request = new HttpRequest(requestBuffer);

            if (!request.isValid()) {
                responseBuffer.append(HttpUtils.HTTP_BAD_REQUEST_RESPONSE);
            }
            else {
                if (request.getDesiredPath().equals("/")) {
                    responseBuffer.append(HttpUtils.HTTP_OK_RESPONSE);
                }
                //Echo route
                else if (request.getDesiredPath().startsWith("/echo")) {
                    String echoString = request
                            .getDesiredPath()
                            .substring("/echo".length()+1);

                    responseBuffer
                            .append(HttpUtils.HTTP_OK_RESPONSE)
                            .append(HttpUtils.HTTP_NEW_LINE)
                            .append("Content-Type: text/plain")
                            .append(HttpUtils.HTTP_NEW_LINE)
                            .append("Content-Length: ").append(echoString.length())
                            .append(HttpUtils.HTTP_NEW_LINE).append(HttpUtils.HTTP_NEW_LINE)
                            .append(echoString);
                }
                //For files routes
                if (request.getDesiredPath().startsWith("/files")) {
                    if (directory == null) {
                        responseBuffer.append(HttpUtils.HTTP_BAD_REQUEST_RESPONSE);
                    }
                    else {
                        //Extract file name from URL
                        String filename = request
                                .getDesiredPath()
                                .substring("/files".length() + 1);

                        switch (request.getMethod().toUpperCase()) {
                            case "GET":
                                if (FileSystemUtils.fileExists(directory, filename)) {
                                    FileSystemUtils
                                            .getFileBytes(directory, filename)
                                            .ifPresentOrElse(fileBytes -> {
                                                responseBuffer
                                                        .append(HttpUtils.HTTP_OK_RESPONSE)
                                                        .append(HttpUtils.HTTP_NEW_LINE)
                                                        .append("Content-Type: application/octet-stream")
                                                        .append(HttpUtils.HTTP_NEW_LINE)
                                                        .append("Content-Length: ").append(fileBytes.length)
                                                        .append(HttpUtils.HTTP_NEW_LINE).append(HttpUtils.HTTP_NEW_LINE);

                                                for (byte b : fileBytes) responseBuffer.append((char) b);
                                            }, () -> responseBuffer.append(HttpUtils.HTTP_INTERNAL_ERROR_RESPONSE));
                                }
                                else {
                                    responseBuffer.append(HttpUtils.HTTP_NOT_FOUND_RESPONSE);
                                }
                                break;
                            case "POST":
                                if (request.getRequestBody() != null && request.getRequestBody().length() > 0) {
                                    logger.info("Request body -> " + request.getRequestBody());
                                    //TODO
                                    responseBuffer.append(HttpUtils.HTTP_CREATED_RESPONSE);
                                }
                                else {
                                    logger.warn("Request body is null, invalid request...");
                                    responseBuffer.append(HttpUtils.HTTP_BAD_REQUEST_RESPONSE);
                                }
                                break;
                            default:
                                responseBuffer.append(HttpUtils.HTTP_NOT_FOUND_RESPONSE);
                                break;
                        }
                    }
                }
                //Header routes
                else if (request.getHeaderFromRoute().isPresent()) {
                    String headerValue = request.getHeaderFromRoute().get();

                    responseBuffer
                            .append(HttpUtils.HTTP_OK_RESPONSE)
                            .append(HttpUtils.HTTP_NEW_LINE)
                            .append("Content-Type: text/plain")
                            .append(HttpUtils.HTTP_NEW_LINE)
                            .append("Content-Length: ").append(headerValue.length())
                            .append(HttpUtils.HTTP_NEW_LINE).append(HttpUtils.HTTP_NEW_LINE)
                            .append(headerValue);
                }
                else {
                    responseBuffer.append(HttpUtils.HTTP_NOT_FOUND_RESPONSE);
                }
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
