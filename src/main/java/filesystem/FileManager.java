package filesystem;

import logger.ApplicationLogger;
import logger.ApplicationLoggerFactory;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class FileManager {

    private static final ApplicationLogger logger = ApplicationLoggerFactory.getLogger(FileManager.class);
    private static final Map<String, FileManager> workingDirectoryToFileManagerMap = new ConcurrentHashMap<>();
    private final String workingDirectory;

    //Ensure singleton instances
    public static FileManager getInstance(String workingDirectory) {
        FileManager instance = workingDirectoryToFileManagerMap.get(workingDirectory);
        if (instance == null) {
            instance = new FileManager(workingDirectory);
            workingDirectoryToFileManagerMap.put(workingDirectory, instance);
        }
        return instance;
    }

    public boolean fileExists(String filename) {
        Path filePath = getPathFromFilename(filename);
        logger.info("Checking if exists " + filePath);
        return Files.exists(filePath);
    }

    public byte[] getFileContent(String filename) throws IOException {
        Path filePath = getPathFromFilename(filename);
        logger.info("Getting file content for " + filePath);
        return Files.readAllBytes(filePath);
    }

    public void writeToFile(StringBuffer content, String filename) throws IOException {
        Path filePath = getPathFromFilename(filename);
        logger.info("Writing content to filename " + filePath);
        Files.writeString(filePath, content.toString());
    }

    public void createFile(String filename) throws IOException {
        Path filePath = getPathFromFilename(filename);
        logger.info("Creating filename " + filePath);
        writeToFile(new StringBuffer(), filename);
    }

    public int getFileLength(String filename) throws IOException {
        return (int) Files.size(getPathFromFilename(filename));
    }

    private Path getPathFromFilename(String filename) {
        return Path.of(workingDirectory, filename);
    }

}
