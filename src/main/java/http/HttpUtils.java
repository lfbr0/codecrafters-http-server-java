package http;

public class HttpUtils {

    public static final String HTTP_HEADER_SEPERATOR = ":";
    public static final String HTTP_NEW_LINE = "\r\n";
    public static final String HTTP_DELIMITER = "\r\n\r\n";
    public static final String HTTP_HEADER_REGEX = "^[A-Za-z0-9-]+: .*$";

    //Responses
    public static final String HTTP_OK_RESPONSE = "HTTP/1.1 200 OK";
    public static final String HTTP_CREATED_RESPONSE = "HTTP/1.1 201 Created";
    public static final String HTTP_BAD_REQUEST_RESPONSE = "HTTP/1.1 400 Bad Request";
    public static final String HTTP_NOT_FOUND_RESPONSE = "HTTP/1.1 404 Not Found";
    public static final String HTTP_INTERNAL_ERROR_RESPONSE = "HTTP/1.1 500 Internal Error";

}
