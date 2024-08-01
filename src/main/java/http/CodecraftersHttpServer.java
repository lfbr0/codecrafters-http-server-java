package http;

import http.handler.ClientHandler;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Runtime.getRuntime;

public class CodecraftersHttpServer {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(CodecraftersHttpServer.class);
    private static final int HTTP_PORT = 4221;
    private static final double DEFAULT_SCALING_FACTOR = 2.0;

    //Object instance fields
    private final ServerSocket serverSocket;
    private final double scalingFactor;
    private final ExecutorService executorService;
    private final AtomicBoolean shutdownFlagAtomicReference;
    private final String workingDirectory;

    public CodecraftersHttpServer(String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        this.serverSocket = new ServerSocket(HTTP_PORT);
        this.scalingFactor = DEFAULT_SCALING_FACTOR;
        this.executorService = Executors.newFixedThreadPool((int) (getRuntime().availableProcessors() * scalingFactor));
        this.shutdownFlagAtomicReference = new AtomicBoolean(false);
    }

    public void start() throws IOException {
        logger.info("Starting up HTTP server for Codecrafters challenge, listening at port " + HTTP_PORT);

        this.serverSocket.setReuseAddress(true);
        while (!this.shutdownFlagAtomicReference.getAcquire()) {
            Socket clientSocket = this.serverSocket.accept();
            logger.info("Accepting client connection -> " + clientSocket.getInetAddress());
            this.executorService.submit(new ClientHandler(clientSocket, workingDirectory));
        }
        
        logger.info("Initiating Shutdown procedure for HTTP server since shutdown flag is activated");
        shutdown();
    }

    public void stop() {
        logger.info("Received shutdown signal, setting shutdown flag as true");
        this.shutdownFlagAtomicReference.set(true);
    }

    private void shutdown() {
        try {
            if (!this.serverSocket.isClosed()) {
                logger.info("Closing server socket");
                this.serverSocket.close();
            }
            if (!this.executorService.isShutdown()) {
                logger.info("Shutting down thread pool");
                this.executorService.shutdownNow();
            }
        }
        catch (IOException e) {
            logger.error("Failed to complete shutdown procedure", e);
        }
    }

}
