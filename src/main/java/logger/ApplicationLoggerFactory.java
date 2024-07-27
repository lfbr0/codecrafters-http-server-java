package logger;

public class ApplicationLoggerFactory {

    public static ApplicationLogger getLogger(Class classObj) {
        return new ApplicationLogger(classObj.getCanonicalName());
    }

    public static ApplicationLogger getLogger(String instanceName) {
        return new ApplicationLogger(instanceName);
    }

}
