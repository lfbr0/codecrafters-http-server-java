package http.encoding;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

public class GzipEncoder implements GenericEncoder {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(GzipEncoder.class);

    @Override
    public StringBuffer encode(StringBuffer body) {
        return body;
    }

}
