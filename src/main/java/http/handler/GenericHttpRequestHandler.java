package http.handler;

import http.models.HttpRequest;
import http.models.HttpResponse;

public interface GenericHttpRequestHandler {

    HttpResponse handleRequest(HttpRequest request);

    default HttpResponse handleNotFoundPathRequest(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(404)
                .statusText("Not Found")
                .build();
    }

    default HttpResponse handleNotFoundPathRequest() {
        return handleNotFoundPathRequest(null);
    }

}
