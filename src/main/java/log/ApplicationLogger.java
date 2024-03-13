package log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationLogger {

    private static final Map<String, ApplicationLogger> instanceMap = new ConcurrentHashMap<>();
    private final Class loggedClass;

    private ApplicationLogger(Class loggedClass) {
        this.loggedClass = loggedClass;
    }

    public static ApplicationLogger getInstance(Class classObj) {
        String desiredClass = classObj.getCanonicalName();

        if (instanceMap.containsKey(desiredClass)) {
            return instanceMap.get(desiredClass);
        }

        ApplicationLogger logger = new ApplicationLogger(classObj);
        instanceMap.put(desiredClass, logger);
        return logger;
    }

    public void error(String message, Throwable e) {
        System.err.printf("ERROR - [%s] %s -> %s\n", loggedClass.getCanonicalName(), message, e.getMessage());
        e.printStackTrace(System.err);
    }

    public void info(String message) {
        System.out.printf("INFO - [%s] %s\n", loggedClass.getCanonicalName(), message);
    }
}
