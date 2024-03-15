package connection;

import handler.ClientHandler;
import log.ApplicationLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ServerManager implements AutoCloseable {

    private final ApplicationLogger logger = ApplicationLogger.getInstance(ServerManager.class);

    private final int port;
    private final ExecutorService taskpool;
    private ServerSocket serverSocket;
    private String directory;
    private Set<String> activeConnectionsSet;

    public ServerManager(int port, ExecutorService taskpool) {
        this.port = port;
        this.taskpool = taskpool;
        this.serverSocket = null;
        this.activeConnectionsSet = Collections.synchronizedSet(new HashSet<>());
    }

    public void run() throws IOException{
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        logger.info("Listening for connections...");
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                String socketKey = clientSocket.getRemoteSocketAddress().toString();
                logger.info("Checking if connection for socket key is unique -> " + socketKey);

                if ( !activeConnectionsSet.contains(socketKey) ) {
                    logger.info("Got connection -> " + clientSocket);
                    clientSocket.setKeepAlive(false);
                    taskpool.submit(new ClientHandler(clientSocket, directory));
                    activeConnectionsSet.add(socketKey);
                }
            } catch (SocketException ex) {
                logger.warn("Got socket exception -> " + ex.getMessage());
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (serverSocket != null && !serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            serverSocket.close();
        }
    }

    public void setDirectory(String directory) {
        logger.info("Setting directory of server as " + directory);
        this.directory = directory;
    }
}
