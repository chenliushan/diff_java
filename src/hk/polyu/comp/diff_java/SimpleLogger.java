package hk.polyu.comp.diff_java;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

    private Path path;
    private Writer writer;
    private LogLevel level;

    private SimpleLogger(Path path) {
        if (path == null)
            throw new IllegalArgumentException();

        this.path = path;
    }

    private void startLogging(LogLevel level) {
        try {
            this.level = level;
            writer = new BufferedWriter(new FileWriter(path.toString()));
        } catch (Exception e) {
            writer = null;

            System.err.println("Error opening log file: " + path.toString());
            System.exit(1);
        }
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final int DATE_WIDTH = 23;
    public static final int LEVEL_WIDTH = 6;
    public static final int PADDING_WIDTH = DATE_WIDTH + LEVEL_WIDTH + 1;

    public static String logHeadFormat = "%1$" + DATE_WIDTH + "s%2$" + LEVEL_WIDTH + "s %3$s\n";
    public static String logPadding = String.format("%" + PADDING_WIDTH + "s", " ");

    private boolean shouldLog(LogLevel level) {
        return level.isNotHigherThan(this.level);
    }

    private void log(String message, LogLevel logLevel) {
        if (shouldLog(logLevel)) {
            try {
                StringBuilder sb = new StringBuilder();

                String[] lines = message.split("\n");
                sb.append(String.format(logHeadFormat, dateFormat.format(new Date()), logLevel.name(), lines[0]));
                for (int i = 1; i < lines.length; i++) {
                    sb.append(logPadding);
                    sb.append(lines[i]);
                    sb.append('\n');
                }

                log(sb.toString());
            } catch (IOException e) {
            }
        }
    }

    private void log(String message) throws IOException {
        writer.write(message);
        writer.flush();
    }

    private void endLogging() {
        if (writer == null) return;

        try {
            writer.flush();
            writer.close();
            writer = null;
        } catch (IOException e) {
        }
    }

    private static SimpleLogger logger;

    public static void start(Path p, LogLevel level) {
        if (logger != null)
            throw new IllegalStateException();

        logger = new SimpleLogger(p);
        logger.startLogging(level);
    }

    public static void end() {
        if (logger == null)
            throw new IllegalStateException();

        logger.endLogging();
    }

    public static void typeCheck(Object... args) {
        return;
    }

    public static void error(String msg) {
        logger.log(msg, LogLevel.ERROR);
    }

    public static void warn(String msg) {
        logger.log(msg, LogLevel.WARN);
    }

    public static void info(String msg) {
        logger.log(msg, LogLevel.INFO);
    }

    public static void debug(String msg) {
        logger.log(msg, LogLevel.DEBUG);
    }

    public static void trace(String msg) {
        logger.log(msg, LogLevel.TRACE);
    }


    public enum LogLevel {
        OFF(0), ERROR(1), WARN(2), INFO(3), DEBUG(4), TRACE(5), ALL(6);

        private int level;

        LogLevel(int level) {
            this.level = level;
        }

        public boolean isNotHigherThan(LogLevel logLevel) {
            return level <= logLevel.level;
        }
    }

    ;
}
