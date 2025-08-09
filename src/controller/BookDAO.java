package controller;

import database.DBConnection;
import model.Book;

import java.sql.*;
import java.util.*;

public class BookDAO {

	public static void addBook(Book book) {
		String sql = "INSERT INTO books (title, author, available) VALUES (?, ?, ?)";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getAuthor());
			stmt.setInt(3, book.isAvailable() ? 1 : 0);

			stmt.executeUpdate();
			System.out.println("Book added successfully.");
		} catch (SQLException e) {
			System.out.println("Failed to add the book.");
			e.printStackTrace();
		}
	}

	public static List<Book> getAllBooks() {
		List<Book> books = new ArrayList<>();
		String sql = "SELECT * FROM books";

		try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				books.add(new Book(
					rs.getInt("id"),
					rs.getString("title"),
					rs.getString("author"),
					rs.getInt("available") == 1
				));
			}

		} catch (SQLException e) {
			System.out.println("Failed to retrieve books.");
			e.printStackTrace();
		}

		return books;
	}

	public static void updateAvailability(int bookId, boolean isAvailable) {
		String sql = "UPDATE books SET available = ? WHERE id = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, isAvailable ? 1 : 0);
			stmt.setInt(2, bookId);
			stmt.executeUpdate();

			System.out.println("Book availability updated.");
		} catch (SQLException e) {
			System.out.println("Failed to update book availability.");
			e.printStackTrace();
		}
	}

	public static void deleteBook(int bookId) {
		String sql = "DELETE FROM books WHERE id = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, bookId);
			stmt.executeUpdate();

			System.out.println("Book deleted successfully.");
		} catch (SQLException e) {
			System.out.println("Failed to delete the book.");
			e.printStackTrace();
		}
	}
}
