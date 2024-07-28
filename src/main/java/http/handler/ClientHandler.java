package http.handler;

import http.middleware.EncodingMiddleware;
import http.models.HttpRequest;
import http.models.HttpResponse;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final ApplicationLogger instanceLogger;
    private final Socket socket;
    private final String workingDirectory;
    private final EncodingMiddleware encodingMiddleware;

    public ClientHandler(Socket socket, String workingDirectory) {
        this.instanceLogger = ApplicationLoggerFactory.getLogger(
                socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
        );
        this.socket = socket;
        this.workingDirectory = workingDirectory;
        this.encodingMiddleware = new EncodingMiddleware();
    }

    @Override
    public void run() {
        /**
         * First parse the request: figure out method, URL, etc.
         * Then handle the request
         * Then answer request & close connection
         */
        try {
            HttpRequest httpRequest = HttpRequest.parse(this.socket.getInputStream());

            //If not valid request, end it here.
            if (!httpRequest.isValidRequest()) {
                answerRequestWithError(
                        "Request is not valid",
                        400,
                        "Bad Request",
                        httpRequest
                );
                return;
            }

            //Determine handler to use according to request method & retrieve processed response
            HttpResponse httpResponse;
            GenericHttpRequestHandler requestHandler = null;
            switch (httpRequest.getMethod()) {
                case GET -> requestHandler = new GetHttpRequestHandler(workingDirectory);
                case POST -> requestHandler = new PostHttpRequestHandler(workingDirectory);
            }

            //If not a valid request handler, than something went very very wrong
            if (requestHandler == null) {
                answerRequestWithError(
                        "Failed to determine request handler",
                        500,
                        "Internal Server Error",
                        httpRequest
                );
                return;
            }

            //Process request & retrieve response
            httpResponse = requestHandler.handleRequest(httpRequest);

            //Apply encoding middleware
            encodingMiddleware.applyMiddleware(httpRequest, httpResponse);

            //Finally answer request with http response
            answerRequest(httpResponse);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            answerRequestWithError(
                    "Failed to handle/process HTTP client\n" + ex.getMessage(),
                    500,
                    "Internal Server Error",
                    null
            );
        }
    }


    private void answerRequest(HttpResponse httpResponse) {
        instanceLogger.info(
                "Finalizing request for instance " +
                        instanceLogger.getInstanceName() +
                        " with response " + httpResponse
        );

        try {
            //Write response to output stream
            OutputStream outputStream = this.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(httpResponse.toString());

            //Close connection
            instanceLogger.info("Closing http client instance " + instanceLogger.getInstanceName());
            writer.close();
            outputStream.close();
        } catch (IOException ex) {
            instanceLogger.error("Failed to answer request with response to client", ex);
        }
    }

    private void answerRequestWithError(String message, int statusCode, String statusText, HttpRequest httpRequest) {
        instanceLogger.error(message + (httpRequest != null ? "\n" + httpRequest : ""));
        answerRequest(
                HttpResponse
                        .builder()
                        .statusCode(statusCode)
                        .statusText(statusText)
                        .build()
        );
    }

}
