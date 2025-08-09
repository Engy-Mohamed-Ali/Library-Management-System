package controller;

import database.DBConnection;
import model.User;
import util.PasswordUtil;
import util.LoginAttemptTracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

	public static User login(String username, String password) {

		if (LoginAttemptTracker.isBlocked(username)) {
			System.out.println("Too many failed attempts. Try again later.");
			return null;
		}

		String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			String hashedPass = PasswordUtil.hash(password);
			stmt.setString(1, username);
			stmt.setString(2, hashedPass);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				LoginAttemptTracker.resetAttempts(username);
				return new User(
					rs.getInt("id"),
					rs.getString("username"),
					rs.getString("role")
				);
			} else {
				LoginAttemptTracker.recordFailedAttempt(username);
			}

		} catch (Exception e) {
			System.out.println("Login failed: " + e.getMessage());
		}
		return null;
	}

	public static boolean changePassword(String username, String oldPass, String newPass) {
		String checkSql = "SELECT * FROM users WHERE username = ? AND password = ?";
		String updateSql = "UPDATE users SET password = ? WHERE username = ?";
		try (Connection conn = DBConnection.connect()) {

			String hashedOld = PasswordUtil.hash(oldPass);
			PreparedStatement checkStmt = conn.prepareStatement(checkSql);
			checkStmt.setString(1, username);
			checkStmt.setString(2, hashedOld);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {
				String hashedNew = PasswordUtil.hash(newPass);
				PreparedStatement updateStmt = conn.prepareStatement(updateSql);
				updateStmt.setString(1, hashedNew);
				updateStmt.setString(2, username);
				int rowsAffected = updateStmt.executeUpdate();
				return rowsAffected > 0;
			}
		} catch (Exception e) {
			System.out.println("Password change failed: " + e.getMessage());
		}
		return false;
	}

	public static void fixPasswordsIfNeeded() {
		String selectSql = "SELECT username, password FROM users";
		String updateSql = "UPDATE users SET password = ? WHERE username = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement selectStmt = conn.prepareStatement(selectSql); ResultSet rs = selectStmt.executeQuery()) {

			while (rs.next()) {
				String username = rs.getString("username");
				String password = rs.getString("password");

				if (password.length() < 20) {
					String hashed = PasswordUtil.hash(password);

					try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
						updateStmt.setString(1, hashed);
						updateStmt.setString(2, username);
						updateStmt.executeUpdate();
						System.out.println("Password updated for user: " + username);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to fix passwords: " + e.getMessage());
		}
	}
}
