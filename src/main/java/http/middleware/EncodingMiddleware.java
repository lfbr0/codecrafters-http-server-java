package http.middleware;

import http.encoding.DummyEncoder;
import http.encoding.GenericEncoder;
import http.encoding.GzipEncoder;
import http.models.HttpRequest;
import http.models.HttpResponse;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.util.List;

public class EncodingMiddleware implements GenericMiddleware {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(EncodingMiddleware.class);

    @Override
    public void applyMiddleware(HttpRequest httpRequest, HttpResponse httpResponse) {
        //This middleware will only do something if request has Accept-Encoding header
        httpRequest
                .getHeader("Accept-Encoding")
                .map(encodingSchemesRawString -> List.of(encodingSchemesRawString.split(",")))
                .ifPresent(encodingSchemes -> {
                    logger.info("User specifies encoding scheme(s) " + encodingSchemes);
                    GenericEncoder encoder;

                    if (encodingSchemes.contains("gzip")) {
                        encoder = new GzipEncoder();
                        httpResponse.putHeader("Content-Encoding", "gzip");
                    }
                    else {
                        encoder = new DummyEncoder();
                    }

                    //Replace body with encoded body & put new content length
                    StringBuffer encodedBody = encoder.encode(httpResponse.getBody());
                    httpResponse.setBody(encodedBody);
                    httpResponse.putHeader("Content-Length", Integer.toString(encodedBody.length()));
                });
    }

}
