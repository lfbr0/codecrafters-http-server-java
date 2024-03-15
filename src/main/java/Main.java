import connection.ServerManager;
import handler.MainShutdownHandler;
import log.ApplicationLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static final int HTTP_DEFAULT_PORT = 4221;

    private static final ApplicationLogger logger = ApplicationLogger.getInstance(Main.class);
    private static final int DEFAULT_CLIENT_TIMEOUT_MS = 10000;

    public static void main(String[] args) {
        logger.info("Starting HTTP server!");
        try {
            final ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ServerManager serverManager = new ServerManager(HTTP_DEFAULT_PORT, threadpool, DEFAULT_CLIENT_TIMEOUT_MS);

            //For directory set...
            if (args.length == 2 && args[0].equalsIgnoreCase("--directory")) {
                serverManager.setDirectory(args[1]);
            }

            //Add callback in case of closing
            Runtime.getRuntime().addShutdownHook(new MainShutdownHandler(serverManager, threadpool));

            serverManager.run();
        } catch (Exception e) {
            logger.error("An error occured in the main proccess", e);
        }
    }

}
