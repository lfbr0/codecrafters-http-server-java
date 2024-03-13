package handler;

import connection.ServerManager;
import log.ApplicationLogger;

import java.util.concurrent.ExecutorService;

public class MainShutdownHandler extends Thread {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance(MainShutdownHandler.class);
    private final ServerManager serverManager;
    private final ExecutorService mainThreadPool;

    public MainShutdownHandler(ServerManager serverManager, ExecutorService mainThreadPool) {
        this.serverManager = serverManager;
        this.mainThreadPool = mainThreadPool;
    }

    @Override
    public void run() {
        logger.info("Executing shutdown procedure...");
        try {
            serverManager.close();
            if (mainThreadPool != null && !mainThreadPool.isShutdown()) {
                mainThreadPool.shutdown();
            }
        } catch (Exception e) {
            logger.error("Error in executing shutdown procedure", e);
        }
        logger.info("Done with shutdown procedure...");
    }

}
