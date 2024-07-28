package logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationLoggerFactory {

    //Avoid multiple instances of logger for same class/instance
    private static final Map<String, ApplicationLogger> instanceNameToLoggerMap = new ConcurrentHashMap<>();

    private static ApplicationLogger getLoggerOrCreate(String instanceName) {
        return instanceNameToLoggerMap.computeIfAbsent(instanceName, ApplicationLogger::new);
    }

    public static ApplicationLogger getLogger(Class classObj) {
        return getLoggerOrCreate(classObj.getCanonicalName());
    }

    public static ApplicationLogger getLogger(String instanceName) {
        return getLoggerOrCreate(instanceName);
    }

}
