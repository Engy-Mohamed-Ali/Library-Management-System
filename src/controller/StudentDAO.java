package controller;

import database.DBConnection;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

	public static List<Student> getAllStudents() {
		List<Student> students = new ArrayList<>();
		String sql = "SELECT * FROM students";

		try (Connection conn = DBConnection.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				students.add(new Student(
					rs.getInt("id"),
					rs.getString("name"),
					rs.getString("email")
				));
			}

		} catch (SQLException e) {
			System.out.println("Failed to retrieve students.");
			e.printStackTrace();
		}

		return students;
	}

	public static void addStudent(Student student) {
		String sql = "INSERT INTO students (name, email) VALUES (?, ?)";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, student.getName());
			stmt.setString(2, student.getEmail());
			stmt.executeUpdate();

			System.out.println("Student added successfully.");
		} catch (SQLException e) {
			System.out.println("Failed to add student.");
			e.printStackTrace();
		}
	}

	public static void deleteStudent(int id) {
		String sql = "DELETE FROM students WHERE id = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);
			stmt.executeUpdate();

			System.out.println("Student deleted successfully.");
		} catch (SQLException e) {
			System.out.println("Failed to delete student.");
			e.printStackTrace();
		}
	}

	public static Student getStudentById(int id) {
		String sql = "SELECT * FROM students WHERE id = ?";

		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return new Student(
					rs.getInt("id"),
					rs.getString("name"),
					rs.getString("email")
				);
			}

		} catch (SQLException e) {
			System.out.println("Failed to get student by ID.");
			e.printStackTrace();
		}

		return null;
	}

	public static void updateStudent(Student student) {
		String sql = "UPDATE students SET name = ?, email = ? WHERE id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, student.getName());
			stmt.setString(2, student.getEmail());
			stmt.setInt(3, student.getId());
			stmt.executeUpdate();
			System.out.println("Student updated.");
		} catch (SQLException e) {
			System.out.println("Failed to update student.");
			e.printStackTrace();
		}
	}
}
