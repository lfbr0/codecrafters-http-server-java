package http.middleware;

import http.models.HttpRequest;
import http.models.HttpResponse;

@FunctionalInterface
public interface GenericMiddleware {

    void applyMiddleware(HttpRequest httpRequest, HttpResponse httpResponse);

}
