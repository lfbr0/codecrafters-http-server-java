import connection.ServerManager;
import handler.MainShutdownHandler;
import log.ApplicationLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static int HTTP_DEFAULT_PORT = 4221;

    private static final ApplicationLogger logger = ApplicationLogger.getInstance(Main.class);

    public static void main(String[] args) {
        logger.info("Starting HTTP server!");
        try {
            final ExecutorService threadpool = Executors.newFixedThreadPool(3);
            ServerManager serverManager = new ServerManager(HTTP_DEFAULT_PORT, threadpool);

            //Add callback in case of closing
            Runtime.getRuntime().addShutdownHook(new MainShutdownHandler(serverManager, threadpool));

            serverManager.init();
        } catch (Exception e) {
            logger.error("An error occured in the main proccess", e);
        }
    }

}
