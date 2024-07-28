package http.middleware;

import http.models.HttpRequest;
import http.models.HttpResponse;

public interface GenericMiddleware {

    public void applyMiddleware(HttpRequest httpRequest, HttpResponse httpResponse);

}
