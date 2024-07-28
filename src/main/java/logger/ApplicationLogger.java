package logger;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

import static java.lang.String.format;

@Getter
public class ApplicationLogger {

    //Default format
    private static final String LOG_FORMAT = "%s [%s] @ %s: %s";
    //Colors for terminal
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    //Instance object fields
    private final String instanceName;

    public ApplicationLogger(String instanceName) {
        this.instanceName = instanceName;
    }

    private void log(LogType logType, String message) {
        if (logType != null) {
            String formattedMessage = format(
                    LOG_FORMAT,
                    logType.name(),
                    getCurrentTimeString(),
                    this.instanceName,
                    message
            );

            switch (logType) {
                case INFO -> System.out.println(formattedMessage);
                case WARN -> System.out.println(ANSI_YELLOW + formattedMessage + ANSI_RESET);
                case ERROR -> System.out.println(ANSI_RED + formattedMessage + ANSI_RESET);
            }
        }
    }

    private String getCurrentTimeString() {
        return LocalDateTime
                .now(Clock.systemUTC())
                .toString();
    }

    public void info(String message) {
        log(LogType.INFO, message);
    }

    public void error(String message) {
        log(LogType.ERROR, message);
    }

    public void warn(String message) {
        log(LogType.WARN, message);
    }

    public void error(String message, Exception ex) {
        error(message + "\n" + ex.getMessage());
        ex.printStackTrace();
    }

}
