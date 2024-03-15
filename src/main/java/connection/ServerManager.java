package connection;

import handler.ClientHandler;
import log.ApplicationLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ServerManager implements AutoCloseable {

    private final ApplicationLogger logger = ApplicationLogger.getInstance(ServerManager.class);

    private final int port;
    private final ExecutorService taskpool;
    private ServerSocket serverSocket;

    public ServerManager(int port, ExecutorService taskpool) {
        this.port = port;
        this.taskpool = taskpool;
        this.serverSocket = null;
    }

    public void run() throws IOException{
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        logger.info("Listening for connections...");
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            logger.info("Got connection -> " + clientSocket);
            clientSocket.setKeepAlive(false);
            taskpool.submit(new ClientHandler(clientSocket));
        }
    }

    @Override
    public void close() throws Exception {
        if (serverSocket != null && !serverSocket.isClosed()) {
            logger.info("Closing server socket...");
            serverSocket.close();
        }
    }

}
