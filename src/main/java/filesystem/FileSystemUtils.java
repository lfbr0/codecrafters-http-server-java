package filesystem;

import log.ApplicationLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileSystemUtils {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance(FileSystemUtils.class);

    public static boolean fileExists(String directory, String fileToCheck) {
        if (directory == null || directory.trim().isEmpty()) {
            logger.warn("Directory is empty/null!");
            return false;
        }

        if (fileToCheck == null || fileToCheck.trim().isEmpty()) {
            logger.warn("Cannot check if dir " + directory + " has file since filename is empty/null!");
            return false;
        }

        Path path = Path.of(directory, fileToCheck);
        logger.info("Checking if file " + path + " exists...");
        return Files.exists(path);
    }

    public static Optional<byte[]> getFileBytes(String directory, String file) {
        if (directory == null || directory.trim().isEmpty()) {
            logger.warn("Directory is empty/null!");
            return Optional.empty();
        }

        if (file == null || file.trim().isEmpty()) {
            logger.warn("Cannot check if dir " + directory + " has file since filename is empty/null!");
            return Optional.empty();
        }

        Path path = Path.of(directory, file);
        try {
            return Optional.of( Files.readAllBytes(path) );
        } catch (IOException ex) {
            logger.error("Failed to read file " + path + " bytes", ex);
            return Optional.empty();
        }
    }

    public static void writeToFile(String directory, String filename, StringBuffer content) throws IOException {
        Path path = Path.of(directory, filename);
        logger.info("Writing to file " + path);
        Files.write(path, content.toString().getBytes(StandardCharsets.UTF_8));
    }

}
