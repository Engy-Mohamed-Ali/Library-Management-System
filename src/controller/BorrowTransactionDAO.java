package controller;

import database.DBConnection;
import model.BorrowTransaction;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorrowTransactionDAO {

	public static void addTransaction(BorrowTransaction transaction) {
		String sql = "INSERT INTO borrow_transactions (book_id, student_id, borrow_date, return_date) VALUES (?, ?, ?, NULL)";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, transaction.getBookId());
			stmt.setInt(2, transaction.getStudentId());
			stmt.setString(3, transaction.getBorrowDate());
			stmt.executeUpdate();
			System.out.println("Borrow transaction recorded.");
		} catch (SQLException e) {
			System.out.println("Failed to record borrow transaction.");
			e.printStackTrace();
		}
	}

	public static void returnBook(int transactionId, String returnDate) {
		String sql = "UPDATE borrow_transactions SET return_date = ? WHERE id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, returnDate);
			stmt.setInt(2, transactionId);
			stmt.executeUpdate();
			System.out.println("Book returned successfully.");
		} catch (SQLException e) {
			System.out.println("Failed to return book.");
			e.printStackTrace();
		}
	}

	public static List<BorrowTransaction> getAllTransactions() {
		List<BorrowTransaction> transactions = new ArrayList<>();
		String sql = "SELECT * FROM borrow_transactions";
		try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				transactions.add(new BorrowTransaction(
					rs.getInt("id"),
					rs.getInt("book_id"),
					rs.getInt("student_id"),
					rs.getString("borrow_date"),
					rs.getString("return_date")
				));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch transactions.");
			e.printStackTrace();
		}
		return transactions;
	}

	public static List<BorrowTransaction> getActiveTransactions() {
		List<BorrowTransaction> transactions = new ArrayList<>();
		String sql = "SELECT * FROM borrow_transactions WHERE return_date IS NULL";
		try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				transactions.add(new BorrowTransaction(
					rs.getInt("id"),
					rs.getInt("book_id"),
					rs.getInt("student_id"),
					rs.getString("borrow_date"),
					null
				));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch active borrowings.");
			e.printStackTrace();
		}
		return transactions;
	}

	public static List<String[]> getTransactionDetails() {
		return getTransactionDetails(null, null);
	}

	public static List<String[]> getTransactionDetails(Date from, Date to) {
		List<String[]> result = new ArrayList<>();
		StringBuilder sql = new StringBuilder("""
			SELECT b.title AS book_title,
			       s.name AS student_name,
			       t.borrow_date,
			       t.return_date
			FROM borrow_transactions t
			JOIN books b ON t.book_id = b.id
			JOIN students s ON t.student_id = s.id
		""");

		List<Object> params = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		if (from != null && to != null) {
			sql.append(" WHERE t.borrow_date BETWEEN ? AND ? ");
			params.add(sdf.format(from));
			params.add(sdf.format(to));
		} else if (from != null) {
			sql.append(" WHERE t.borrow_date >= ? ");
			params.add(sdf.format(from));
		} else if (to != null) {
			sql.append(" WHERE t.borrow_date <= ? ");
			params.add(sdf.format(to));
		}

		sql.append(" ORDER BY t.borrow_date DESC");

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				stmt.setString(i + 1, params.get(i).toString());
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String bookTitle = rs.getString("book_title");
				String studentName = rs.getString("student_name");
				String borrowDate = rs.getString("borrow_date");
				String returnDate = rs.getString("return_date");

				if (returnDate == null) {
					returnDate = "Not returned";
				}

				result.add(new String[]{bookTitle, studentName, borrowDate, returnDate});
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch filtered transaction details.");
			e.printStackTrace();
		}
		return result;
	}
}
