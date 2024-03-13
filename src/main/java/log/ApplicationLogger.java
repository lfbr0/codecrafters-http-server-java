package log;

import handler.ClientHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationLogger {

    private static final Map<String, ApplicationLogger> instanceMap = new ConcurrentHashMap<>();
    private final Class loggedClass;
    private String identifier;

    public enum ApplicationLoggerLevel {
        INFO, ERROR;
    }

    //PRIVATE METHODS

    private ApplicationLogger(Class loggedClass) {
        this.loggedClass = loggedClass;
    }

    private ApplicationLogger(Class loggedClass, String identifier) {
        this.loggedClass = loggedClass;
        this.identifier = identifier;
    }

    private String getFormat(ApplicationLoggerLevel level) {
        return Optional
                .ofNullable(identifier)
                .map(id -> String.format("%s - [%s]:[%s]" , level.name(), loggedClass.getCanonicalName(), id))
                .orElse(String.format("%s - [%s]", level.name(), loggedClass.getCanonicalName()));
    }

    private String getLogString(ApplicationLoggerLevel level, String message) {
        return getFormat(level) + " " + message;
    }

    //INSTANTIATION METHODS

    public static ApplicationLogger getInstance(Class classObj) {
        String desiredClass = classObj.getCanonicalName();

        if (instanceMap.containsKey(desiredClass)) {
            return instanceMap.get(desiredClass);
        }

        ApplicationLogger logger = new ApplicationLogger(classObj);
        instanceMap.put(desiredClass, logger);
        return logger;
    }

    public static ApplicationLogger getPrototypeInstance(Class classObj, String identifier) {
        return new ApplicationLogger(classObj, identifier);
    }

    //LOGGING METHODS

    public void error(String message, Throwable e) {
        System.err.println(getLogString(ApplicationLoggerLevel.ERROR, message + " -> " + e.getMessage()));
        e.printStackTrace(System.err);
    }

    public void info(String message) {
        System.out.println(getLogString(ApplicationLoggerLevel.INFO, message));
    }

}
