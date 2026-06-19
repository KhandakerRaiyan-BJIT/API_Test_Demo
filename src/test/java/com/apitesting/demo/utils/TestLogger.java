package com.apitesting.demo.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TestLogger {
    private static final Object LOCK = new Object();
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_PATH = Paths.get(APIConstants.LOG_FILE_PATH);

    private TestLogger() {
    }

    public static void initialize() {
        synchronized (LOCK) {
            try {
                Path parent = LOG_PATH.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.writeString(
                        LOG_PATH,
                        "===== Test Run Started: " + LocalDateTime.now().format(TS_FORMAT) + " =====" + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize log file at " + LOG_PATH, e);
            }
        }
    }

    public static void log(String testName, String message) {
        String line = String.format("[%s] [%s] %s%s", LocalDateTime.now().format(TS_FORMAT), testName, message, System.lineSeparator());
        synchronized (LOCK) {
            try {
                Files.writeString(
                        LOG_PATH,
                        line,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to write test log", e);
            }
        }
    }

    public static String readAll() {
        synchronized (LOCK) {
            try {
                if (!Files.exists(LOG_PATH)) {
                    return "Log file is not available yet.";
                }
                return Files.readString(LOG_PATH, StandardCharsets.UTF_8);
            } catch (IOException e) {
                return "Unable to read log file: " + e.getMessage();
            }
        }
    }

    public static String logPath() {
        return LOG_PATH.toString();
    }
}

