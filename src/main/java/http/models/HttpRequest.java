package http.models;

import http.CodecraftersHttpServer;
import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;
import lombok.Builder;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;

@Builder
@Getter
public class HttpRequest {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(HttpRequest.class);
    private static final String HEADER_REGEX_PATTERN = ".+:.+";
    private static final String CONTENT_LENGTH_HEADER_NAME = "Content-Length";
    private static final int HTTP_BODY_BUFFER_SIZE = 4096;

    @Builder.Default
    private boolean validRequest = true;

    private HttpRequestMethod method;
    private String path;
    private String version;
    private StringBuffer body;
    private Map<String, String> headers;

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        //Request variables
        HttpRequest.HttpRequestBuilder httpRequestBuilder = new HttpRequest.HttpRequestBuilder();
        Map<String, String> parsedHeaders = new HashMap<>();

        /**
         * While request lines from HTTP request aren't empty, keeping reading & incrementing line counter
         */
        int requestLineCount = 0;
        for (String reqLine = reader.readLine(); reqLine != null && !reqLine.isEmpty(); reqLine = reader.readLine()) {
            //If first line, then it's HTTP request method, etc
            if (requestLineCount == 0) {
                String[] requestParams = reqLine.split("\\s");

                //If request parameters are not METHOD PATH VERSION, then stop it here
                if (requestParams.length != 3) {
                    logger.warn("Request parameters are bad, line doesn't follow METHOD PATH VERSION: " + reqLine);
                    httpRequestBuilder = httpRequestBuilder.validRequest(false);
                    break;
                }

                //Extract METHOD URL VERSION
                String method = requestParams[0], path = requestParams[1], version = requestParams[2];

                //Check method && version we always assume OK
                if (!HttpRequestMethod.isValidMethod(method)) {
                    logger.warn("Request method is bad: " + reqLine);
                    httpRequestBuilder = httpRequestBuilder.validRequest(false);
                    break;
                }

                //Set extracted request params
                httpRequestBuilder = httpRequestBuilder
                        .method(HttpRequestMethod.from(method))
                        .path(path)
                        .version(version);
            }
            //If not, it's header
            else {
                if (!reqLine.matches(HEADER_REGEX_PATTERN)) {
                    logger.warn("Request header is bad: " + reqLine);
                    httpRequestBuilder = httpRequestBuilder.validRequest(false);
                    break;
                }

                //Extract KEY:VALUE from header
                int seperatorIndex = reqLine.indexOf(":");
                String key = reqLine.substring(0, seperatorIndex).trim();
                String value = reqLine.substring(seperatorIndex + 1).trim();

                //place into parsed map
                parsedHeaders.put(key, value);
            }

            //Increment request line counter
            requestLineCount++;
        }

        //Request is valid, no issues found

        //Place parsed headers map into request builder
        httpRequestBuilder = httpRequestBuilder.headers(parsedHeaders);

        //If parsed headers contains content length, then we can expect a body
        String contentLengthRaw = parsedHeaders.getOrDefault(CONTENT_LENGTH_HEADER_NAME, "0");
        try {
            int contentLength = parseInt(contentLengthRaw);

            //If there is content to read, read it
            if (contentLength > 0) {
                //Content can be big, so let's buffer it
                StringBuffer bodyBuffer = new StringBuffer(contentLength);
                char[] buffer = new char[HTTP_BODY_BUFFER_SIZE];

                //Read until content length is reached or end of stream (EOS)
                int totalRead = 0, read = 0;
                while (totalRead < contentLength) {
                    //Read until buffer size or whatever was read
                    read = reader.read(buffer, 0, Math.min(HTTP_BODY_BUFFER_SIZE, contentLength - totalRead));

                    //If we read -1, then reached EOS
                    if (read == -1) break;

                    //Append what was read to body buffer
                    bodyBuffer.append(buffer, 0, read);
                    totalRead += read;
                }

                //Store body buffer in request
                httpRequestBuilder = httpRequestBuilder.body(bodyBuffer);
            }
        } catch (NumberFormatException ex) {
            logger.warn("Bad content length value, non-numeric. Assuming it's 0, no body. Received: " + contentLengthRaw);
        }


        //Build object & return
        return httpRequestBuilder.build();
    }

    public Optional<String> getHeader(String key) {
        return Optional.ofNullable(headers.get(key));
    }

}
