package http.handler;

import http.models.HttpRequest;
import http.models.HttpResponse;

public class GetHttpRequestHandler implements GenericHttpRequestHandler {

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        String path = request.getPath();
        HttpResponse httpResponse = null;

        //According to path, determine handling method & response
        if (path.equals("/")) {
            httpResponse = handleRootPathRequest(request);
        }
        else {
            httpResponse = handleNotFoundPathRequest(request);
        }

        return httpResponse;
    }

    private HttpResponse handleRootPathRequest(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(200)
                .statusText("OK")
                .build();
    }

}
