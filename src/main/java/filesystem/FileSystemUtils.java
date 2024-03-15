package filesystem;

import log.ApplicationLogger;

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
        return true;
    }

}
