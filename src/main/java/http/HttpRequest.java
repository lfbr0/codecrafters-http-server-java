package http;

import log.ApplicationLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class HttpRequest {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance(HttpRequest.class);
    private final Map<String, String> headers;
    private String desiredPath;
    private String method;
    private boolean isValid = true;
    private StringBuffer requestBody = null;

    public HttpRequest(StringBuffer requestBuffer) {
        String requestText = requestBuffer.toString();
        logger.info("START_REQUEST$$$\n" + requestText + "\n$$$END_REQUEST\n");

        StringTokenizer tokenizer = new StringTokenizer(requestText, HttpUtils.HTTP_NEW_LINE);
        this.headers = new HashMap<>();

        for (int lineIdx = 0; tokenizer.hasMoreTokens(); lineIdx++) {
            String line = tokenizer.nextToken();

            //For request
            if (lineIdx == 0) {
                String[] requestParts = line.split("\\s+");
                if (requestParts.length != 3) {
                    logger.warn("This is not a valid request, first line doesn't contain 3 parts -> " + line);
                    this.isValid = false;
                    return;
                }
                else {
                    this.method = requestParts[0];
                    if (!this.method.equalsIgnoreCase("GET") && !this.method.equalsIgnoreCase("POST")) {
                        logger.warn("Request is not valid, is not GET or POST request!");
                        this.isValid = false;
                        return;
                    }

                    if (!requestParts[2].equalsIgnoreCase("HTTP/1.1")) {
                        logger.warn("Request is not valid, is not HTTP/1.1 version!");
                        this.isValid = false;
                        return;
                    }

                    desiredPath = requestParts[1];
                }
            }
            //For headers
            else {

                if (line.matches(HttpUtils.HTTP_HEADER_REGEX)) {
                    String[] header = line.split(HttpUtils.HTTP_HEADER_SEPERATOR, 2);
                    if (header.length == 2) {
                        headers.put(header[0].trim(), header[1].trim());
                    }
                }
                else {
                    if (requestBody == null) {
                        requestBody = new StringBuffer();
                    }
                    requestBody.append(line);
                }

            }
        }
    }

    public String getDesiredPath() {
        return desiredPath;
    }

    public String getMethod() {
        return method;
    }

    public boolean isValid() {
        return isValid;
    }

    public StringBuffer getRequestBody() {
        return requestBody;
    }

    public Optional<String> getHeaderFromRoute() {
        return headers
                .keySet()
                .stream()
                .filter(header -> header.equalsIgnoreCase(desiredPath.substring(1)))
                .findFirst()
                .map(headers::get);
    }

}
