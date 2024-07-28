package http.encoding;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzipEncoder implements GenericEncoder {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(GzipEncoder.class);

    @Override
    public StringBuffer encode(StringBuffer body) {
        String bodyText = body.toString();
        logger.info("Proceeding to encode " + bodyText + " as GZIP");
        byte[] dataToCompress = bodyText.getBytes(StandardCharsets.UTF_8);

        //Convert to Gzip output stream
        ByteArrayOutputStream byteStream = null;
        GZIPOutputStream zipStream = null;
        try {
            byteStream = new ByteArrayOutputStream(dataToCompress.length);
            zipStream = new GZIPOutputStream(byteStream);
            zipStream.write(dataToCompress);
        }
        catch (IOException ex) {
            logger.error("Failed to convert to gzip output stream", ex);
            return body;
        }

        //Close streams
        try {
            zipStream.close();
            byteStream.close();
        }
        catch (IOException ex) {
            logger.error("Failed to close encoding streams", ex);
            return body;
        }

        //Get converted stream bytes and return body
        byte[] compressedData = byteStream.toByteArray();
        StringBuffer convertedBody = new StringBuffer(compressedData.length);
        for (byte compressedByte : compressedData) {
            convertedBody.append((char) compressedByte);
        }

        return convertedBody;
    }

}
