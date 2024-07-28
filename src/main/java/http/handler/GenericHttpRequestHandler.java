package http.handler;

import http.models.HttpRequest;
import http.models.HttpResponse;

import java.io.IOException;

public interface GenericHttpRequestHandler {

    HttpResponse handleRequest(HttpRequest request) throws IOException;

    default HttpResponse handleNotFoundPathRequest(HttpRequest request) {
        return HttpResponse.builder()
                .statusCode(404)
                .statusText("Not Found")
                .build();
    }

}
