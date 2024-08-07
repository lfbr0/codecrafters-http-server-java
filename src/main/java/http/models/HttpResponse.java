package http.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static java.lang.String.format;

@Builder
@Getter
public class HttpResponse {

    private int statusCode;
    private String statusText;

    @Setter
    private StringBuffer body;

    private Map<String, String> headers;

    @Override
    public String toString() {
        StringBuilder responseBuffer = new StringBuilder();
        responseBuffer
                .append("HTTP/1.1").append(" ")
                .append(statusCode).append(" ")
                .append(statusText).append("\r\n");

        if (this.headers != null && !this.headers.isEmpty()) {
            this.headers.forEach((key,value) -> responseBuffer.append(format("%s: %s\r\n", key, value)));
            responseBuffer.append("\r\n");
        }

        if (body != null && !body.isEmpty()) {
            responseBuffer.append(body);
            responseBuffer.append("\r\n");
        }

        return responseBuffer
                .append("\r\n")
                .toString();
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

}
