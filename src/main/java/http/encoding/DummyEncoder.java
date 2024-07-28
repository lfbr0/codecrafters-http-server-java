package http.encoding;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

public class DummyEncoder implements GenericEncoder {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(DummyEncoder.class);

    @Override
    public StringBuffer encode(StringBuffer body) {
        return body;
    }

}
