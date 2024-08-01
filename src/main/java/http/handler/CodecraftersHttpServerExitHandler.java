package http.handler;

import http.CodecraftersHttpServer;
import logger.ApplicationLogger;
import lombok.RequiredArgsConstructor;

import static logger.ApplicationLoggerFactory.getLogger;

@RequiredArgsConstructor
public class CodecraftersHttpServerExitHandler extends Thread {

    private final ApplicationLogger logger = getLogger(CodecraftersHttpServerExitHandler.class);
    private final CodecraftersHttpServer server;

    @Override
    public void run() {
        logger.info("Emitting exit signal to Codecrafters Http Server...");
        server.stop();
    }

}
