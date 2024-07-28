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

        // Convert input string to byte array using UTF-8
        byte[] dataToCompress = bodyText.getBytes(StandardCharsets.UTF_8);

        // Use try-with-resources for automatic resource management
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {

            // Write data to the GZIP output stream
            zipStream.write(dataToCompress);
            zipStream.finish();  // Complete the compression process

            // Get the compressed byte array
            byte[] compressedData = byteStream.toByteArray();

            // Convert the byte array to a hexadecimal string
            StringBuffer hexString = toHexString(compressedData);

            // Return the hexadecimal representation
            return hexString;
        } catch (IOException ex) {
            logger.error("Failed to convert to gzip output stream", ex);
            return body;
        }
    }

    // Helper method to convert byte array to a hexadecimal string
    private StringBuffer toHexString(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : bytes) {
            // Convert each byte to a two-digit hexadecimal value
            String hex = String.format("%02X ", b);
            hexString.append(hex);
        }
        return hexString;
    }
}
