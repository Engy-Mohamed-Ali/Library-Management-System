package database;

import java.sql.*;

public class DBConnection {

	private static final String URL = "jdbc:sqlite:library.db";

	public static Connection connect() {
		try {
			return DriverManager.getConnection(URL);
		} catch (SQLException e) {
			System.out.println("Database connection failed.");
			e.printStackTrace();
			return null;
		}
	}
}
