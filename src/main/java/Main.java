import http.CodecraftersHttpServer;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.IOException;


public class Main {

    private final static ApplicationLogger logger = ApplicationLoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            CodecraftersHttpServer server = new CodecraftersHttpServer();
            server.start();
        }
        catch (IOException ex) {
            logger.error("Failed to open up/start HTTP server", ex);
        }
    }
}
