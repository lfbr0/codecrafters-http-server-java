package http.handler;

import http.models.HttpRequest;
import http.models.HttpResponse;

public interface GenericHttpRequestHandler {

    public HttpResponse handleRequest(HttpRequest request);

    public default HttpResponse handleNotFoundPathRequest(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(404)
                .statusText("Not Found")
                .build();
    }

    public default HttpResponse handleNotFoundPathRequest() {
        return handleNotFoundPathRequest(null);
    }

}
