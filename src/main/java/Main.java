import http.CodecraftersHttpServer;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;


public class Main {

    private final static ApplicationLogger logger = ApplicationLoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String workingDirectory;
            if (args.length == 2 && args[0].equalsIgnoreCase("--directory")) {
                workingDirectory = args[1];
            }
            else {
                workingDirectory = System.getProperty("user.home");
                logger.warn("No directory was set, therefore setting directory as homefolder...");
            }
            logger.info("Setting working directory for HTTP server as " + workingDirectory);

            CodecraftersHttpServer server = new CodecraftersHttpServer(workingDirectory);
            server.start();
        }
        catch (IOException ex) {
            logger.error("Failed to open up/start HTTP server", ex);
        }
    }
}
