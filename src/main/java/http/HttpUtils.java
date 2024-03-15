package http;

public class HttpUtils {

    public static final String HTTP_HEADER_SEPERATOR = ":";
    public static final String HTTP_NEW_LINE = "\r\n";
    public static final String HTTP_DELIMITER = "\r\n\r\n";

    //Responses
    public static final String HTTP_200_RESPONSE = "HTTP/1.1 200 OK";
    public static final String HTTP_400_RESPONSE = "HTTP/1.1 400 Bad Request";
    public static final String HTTP_404_RESPONSE = "HTTP/1.1 404 Not Found";
    public static final String HTTP_500_RESPONSE = "HTTP/1.1 500 Internal Error";

    public static final String HTTP_HEADER_REGEX = "^[A-Za-z0-9-]+: .*$";
}
