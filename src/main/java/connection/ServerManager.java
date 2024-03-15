package connection;

import handler.ClientHandler;
import log.ApplicationLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager {

    private final ApplicationLogger logger = ApplicationLogger.getInstance(ServerManager.class);

    private final int port;
    private final ExecutorService taskpool;
    private ServerSocket serverSocket;
    private String directory;
    private AtomicBoolean closeFlag;

    public ServerManager(int port, ExecutorService taskpool) {
        this.port = port;
        this.taskpool = taskpool;
        this.serverSocket = null;
        this.closeFlag = new AtomicBoolean(false);
    }

    public void setDirectory(String directory) {
        logger.info("Setting directory of server as " + directory);
        this.directory = directory;
    }

    public void run() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        logger.info("Listening for connections...");

        while (!closeFlag.getAcquire()) {
            Socket clientSocket = serverSocket.accept();
            logger.info("Got connection -> " + clientSocket);
            clientSocket.setKeepAlive(false);
            taskpool.submit(new ClientHandler(clientSocket, directory));
        }

        if (!serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            serverSocket.close();
        }
    }

    public void close() throws Exception {
        logger.info("Setting close flag to true...");
        closeFlag.set(true);
    }

}
