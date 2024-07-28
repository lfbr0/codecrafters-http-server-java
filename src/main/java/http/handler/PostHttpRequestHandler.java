package http.handler;

import filesystem.FileManager;
import http.models.HttpRequest;
import http.models.HttpResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class PostHttpRequestHandler implements GenericHttpRequestHandler {

    private final String workingDirectory;

    @Override
    public HttpResponse handleRequest(HttpRequest request) throws IOException {
        String path = request.getPath();
        HttpResponse httpResponse;

        if (path.startsWith("/files/")) {
            httpResponse = handleFilesPathRequest(request);
        }
        else {
            httpResponse = handleNotFoundPathRequest(request);
        }

        return httpResponse;
    }


    /** HANDLER METHODS **/

    private HttpResponse handleFilesPathRequest(HttpRequest request) throws IOException {
        FileManager fileManager = FileManager.getInstance(workingDirectory);
        String filename = request
                .getPath()
                .substring("/files/".length());

        //If file exists, return 304 not modified
        if (fileManager.fileExists(filename)) {
            return HttpResponse.builder()
                    .statusCode(304)
                    .statusText("Not Modified")
                    .build();
        }
        //If it doesn't, create it according to body
        else {
            //Create file with request body
            fileManager.writeToFile(request.getBody(), filename);
            return HttpResponse.builder()
                    .statusCode(201)
                    .statusText("Created")
                    .build();
        }
    }

}
