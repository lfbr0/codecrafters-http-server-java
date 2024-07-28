package http.models;

public enum HttpRequestMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpRequestMethod from(String method) {
        return HttpRequestMethod.valueOf(method.toUpperCase());
    }

    public static boolean isValidMethod(String method) {
        try {
            return from(method) != null;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }

}
