package util;

import database.DBConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LoginAttemptTracker {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BLOCK_MINUTES = 10;

	public static boolean isBlocked(String username) {
		String sql = "SELECT failed_attempts, last_attempt_time FROM login_attempts WHERE username = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int attempts = rs.getInt("failed_attempts");
				Timestamp lastTime = rs.getTimestamp("last_attempt_time");

				if (attempts >= MAX_ATTEMPTS && lastTime != null) {
					LocalDateTime last = lastTime.toLocalDateTime();
					LocalDateTime now = LocalDateTime.now();
					Duration diff = Duration.between(last, now);
					return diff.toMinutes() < BLOCK_MINUTES;
				}
			}
		} catch (Exception e) {
			System.err.println("Login block check failed:");
			e.printStackTrace();
		}
		return false;
	}

	public static void recordFailedAttempt(String username) {
		String selectSql = "SELECT * FROM login_attempts WHERE username = ?";
		String insertSql = "INSERT INTO login_attempts (username, failed_attempts, last_attempt_time) VALUES (?, 1, CURRENT_TIMESTAMP)";
		String updateSql = "UPDATE login_attempts SET failed_attempts = failed_attempts + 1, last_attempt_time = CURRENT_TIMESTAMP WHERE username = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement select = conn.prepareStatement(selectSql)) {
			select.setString(1, username);
			ResultSet rs = select.executeQuery();

			if (rs.next()) {
				try (PreparedStatement update = conn.prepareStatement(updateSql)) {
					update.setString(1, username);
					update.executeUpdate();
				}
			} else {
				try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
					insert.setString(1, username);
					insert.executeUpdate();
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to record failed attempt: " + e.getMessage());
		}
	}

	public static void resetAttempts(String username) {
		String sql = "DELETE FROM login_attempts WHERE username = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Failed to reset attempts: " + e.getMessage());
		}
	}
}
