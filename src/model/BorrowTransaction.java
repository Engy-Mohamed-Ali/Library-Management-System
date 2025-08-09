package model;

public class BorrowTransaction {

	private int id;
	private int bookId;
	private int studentId;
	private String borrowDate;
	private String returnDate;

	public BorrowTransaction() {
	}

	public BorrowTransaction(int id, int bookId, int studentId, String borrowDate, String returnDate) {
		this.id = id;
		this.bookId = bookId;
		this.studentId = studentId;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(String borrowDate) {
		this.borrowDate = borrowDate;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

}
