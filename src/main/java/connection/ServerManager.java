package connection;

import log.ApplicationLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager implements AutoCloseable {

    private final ApplicationLogger logger = ApplicationLogger.getInstance(ServerManager.class);

    private final int port;
    private final ExecutorService taskpool;
    private final List<Socket> clientSockets;

    private ServerSocket serverSocket;
    private Future connectionListenerFuture;

    public ServerManager(int port, ExecutorService taskpool) {
        this.port = port;
        this.taskpool = taskpool;
        this.serverSocket = null;
        this.clientSockets = Collections.synchronizedList(new LinkedList<>());
    }

    public void init() throws IOException, ExecutionException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);

        connectionListenerFuture = taskpool.submit(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Got an incoming connection from " + clientSocket.getRemoteSocketAddress());
                    clientSockets.add(clientSocket);
                } catch (IOException e) {
                    logger.error("Failed to accept client socket", e);
                }
            }
        });

        try {
            connectionListenerFuture.get();
        } catch (InterruptedException e) {
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
        clientSockets.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Failed to close client socket", e);
            }
        });

        if (serverSocket != null && !serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            serverSocket.close();
        }
    }

}
