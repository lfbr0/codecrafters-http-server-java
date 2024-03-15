package filesystem;

import log.ApplicationLogger;

import java.nio.file.Files;
import java.nio.file.Path;

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

        logger.info("Checking if file " + fileToCheck + " exists in dir " + directory);
        Path path = Path.of(directory, fileToCheck);
        logger.info("Path -> " + path);
        return Files.exists(path);
    }

}
