package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {

	private static final String LOG_DIRECTORY = "logs";
	private static final String LOG_FILE = LOG_DIRECTORY + "/operations.log";
	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	static {
		File dir = new File(LOG_DIRECTORY);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void log(String username, String role, String action, String details) {
		String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
		String logEntry = String.format("[%s] [%s - %s] %s → %s", timestamp, username, role.toUpperCase(), action, details);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
			writer.write(logEntry);
			writer.newLine();
		} catch (IOException e) {
			System.err.println("⚠️ Failed to write to log file:");
			e.printStackTrace();
		}
	}
}
