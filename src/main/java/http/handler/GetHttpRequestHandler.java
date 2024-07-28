package http.handler;

import http.models.HttpRequest;
import http.models.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class GetHttpRequestHandler implements GenericHttpRequestHandler {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String path = request.getPath();
        HttpResponse httpResponse = null;

        //According to path, determine handling method & response
        if (path.equals("/")) {
            httpResponse = handleRootPathRequest(request);
        }
        else if (path.startsWith("/echo/")) {
            httpResponse = handleEchoPathRequest(request);
        }
        else {
            httpResponse = handleNotFoundPathRequest(request);
        }

        return httpResponse;
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
