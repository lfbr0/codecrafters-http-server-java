package http.handler;

import filesystem.FileManager;
import http.models.HttpRequest;
import http.models.HttpResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GetHttpRequestHandler implements GenericHttpRequestHandler {

    private final String workingDirectory;

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws IOException {
        String path = request.getPath();
        HttpResponse httpResponse;

        //According to path, determine handling method & response
        if (path.equals("/")) {
            httpResponse = handleRootPathRequest(request);
        }
        else if (path.startsWith("/echo/")) {
            httpResponse = handleEchoPathRequest(request);
        }
        else if (path.startsWith("/user-agent")) {
            httpResponse = handleUserAgentPathRequest(request);
        }
        else if (path.startsWith("/files/")) {
            httpResponse = handleFilesPathRequest(request);
        }
        else {
            httpResponse = handleNotFoundPathRequest(request);
        }

        return httpResponse;
    }


    /** HANDLER METHODS **/

    private HttpResponse handleFilesPathRequest(HttpRequest request) throws IOException {
        /**
         * 1. get instance of file manager
         * 2. get filename from request
         * 3. check if it exists
         * 3.1. if not exists, return 404
         * 3.2. if exists, read file content into byte array and place onto object
         */
        FileManager fileManager = FileManager.getInstance(workingDirectory);
        String filename = request
                .getPath()
                .substring("/files/".length());

        if (!fileManager.fileExists(filename)) {
            return handleNotFoundPathRequest(request);
        }

        //Place file content onto buffer
        int fileBytes = fileManager.getFileLength(filename);
        StringBuffer fileContentBuffer = new StringBuffer(fileBytes);
        for (byte fileByte : fileManager.getFileContent(filename)) {
            fileContentBuffer.append((char) fileByte);
        }

        //Create response headers
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/octet-stream");
        responseHeaders.put("Content-Length", Integer.toString(fileBytes));

        return HttpResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .headers(responseHeaders)
                .body(fileContentBuffer)
                .build();
    }

    private HttpResponse handleUserAgentPathRequest(HttpRequest request) {
        //Get user agent value or nothing if no header present
        String userAgentValue = request
                .getHeader("User-Agent")
                .orElse("");

        //Fill headers
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/plain");
        responseHeaders.put("Content-Length", Integer.toString(userAgentValue.length()));

        //Place response body
        StringBuffer responseBody = new StringBuffer(userAgentValue);

        return HttpResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .headers(responseHeaders)
                .body(responseBody)
                .build();
    }

    private HttpResponse handleEchoPathRequest(HttpRequest request) {
        //Get echo string
        String echoString = request
                .getPath()
                .substring("/echo/".length());

        //Fill headers
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "text/plain");
        responseHeaders.put("Content-Length", Integer.toString(echoString.length()));

        //Place response body
        StringBuffer responseBody = new StringBuffer(echoString);

        return HttpResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .headers(responseHeaders)
                .body(responseBody)
                .build();
    }

    private HttpResponse handleRootPathRequest(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .build();
    }

}
