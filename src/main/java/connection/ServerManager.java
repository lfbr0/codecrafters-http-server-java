package connection;

import handler.ClientHandler;
import log.ApplicationLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager implements AutoCloseable {

    private final ApplicationLogger logger = ApplicationLogger.getInstance(ServerManager.class);

    private final int port;
    private final ExecutorService taskpool;
    private final List<ClientHandler> clientHandlers;

    private ServerSocket serverSocket;
    private Future connectionListenerFuture;

    public ServerManager(int port, ExecutorService taskpool) {
        this.port = port;
        this.taskpool = taskpool;
        this.serverSocket = null;
        this.clientHandlers = Collections.synchronizedList(new LinkedList<>());
    }

    public void init() throws IOException, ExecutionException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);

        connectionListenerFuture = taskpool.submit(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Got an incoming connection from " + clientSocket.getRemoteSocketAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandlers.add(clientHandler);
                    taskpool.submit(clientHandler);
                } catch (IOException e) {
                    logger.error("Failed to accept client socket", e);
                }
            }
        });

        try {
            connectionListenerFuture.get();
        } catch (InterruptedException | CancellationException e) {
            logger.info("Got order to stop listening to incoming client connections...");
        }
    }

    @Override
    public void close() throws Exception {
        if (connectionListenerFuture != null) {
            logger.info("Closing any listening...");
            connectionListenerFuture.cancel(true);
        }

        logger.info("Closing down client sockets...");
        clientHandlers.forEach(handler -> {
            try {
                handler.close();
            } catch (IOException e) {
                logger.error("Failed in closing handler", e);
            }
        });

        if (serverSocket != null && !serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            serverSocket.close();
        }
    }

}
