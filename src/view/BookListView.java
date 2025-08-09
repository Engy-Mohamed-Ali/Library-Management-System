package view;

import controller.BookDAO;
import controller.BorrowTransactionDAO;
import controller.StudentDAO;
import model.Book;
import model.BorrowTransaction;
import model.Student;
import model.User;
import util.LoggerUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class BookListView extends JFrame {

	private JTable table;
	private DefaultTableModel tableModel;
	private BufferedImage backgroundImage;
	private JTextField searchField;
	private JButton searchButton;
	private JButton resetButton;
	private MainDashboard dashboardRef;
	private User loggedInUser;

	public BookListView(MainDashboard dashboardRef, User user) {
		this.dashboardRef = dashboardRef;
		this.loggedInUser = user;

		util.AppIconLoader.applyAppIcon(this);
		setTitle("Library - Book List");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			backgroundImage = ImageIO.read(new File("assets/background.png"));
		} catch (Exception e) {
		}

		JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		userInfoPanel.setOpaque(false);
		JLabel userLabel = new JLabel("👤 Logged in as: " + loggedInUser.getUsername() + " [" + loggedInUser.getRole().toUpperCase() + "]");
		userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		userLabel.setForeground(new Color(20, 20, 60));
		userInfoPanel.add(userLabel);
		add(userInfoPanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (backgroundImage != null) {
					g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
				}
			}
		};
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);

		JPanel searchPanel = new JPanel();
		searchPanel.setOpaque(false);
		searchField = new JTextField(20);
		searchButton = new JButton("Search");
		searchButton.setForeground(new Color(20, 20, 60));
		searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		searchButton.setBackground(new Color(200, 200, 200));
		resetButton = new JButton("Reset");
		resetButton.setForeground(new Color(20, 20, 60));
		resetButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		resetButton.setBackground(new Color(200, 200, 200));
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		searchPanel.add(resetButton);
		contentPanel.add(searchPanel, BorderLayout.NORTH);
		searchButton.addActionListener(e -> searchBooks());
		resetButton.addActionListener(e -> loadBooks());

		tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Available"}, 0);
		table = new JTable(tableModel);
		table.setFont(new Font("SansSerif", Font.PLAIN, 14));
		table.setForeground(Color.WHITE);
		table.setBackground(new Color(0, 0, 0, 150));
		table.setGridColor(Color.LIGHT_GRAY);
		table.setRowHeight(30);
		table.setOpaque(false);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("SansSerif", Font.BOLD, 16));
		header.setBackground(new Color(230, 230, 230));
		header.setForeground(new Color(20, 20, 60));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);

		JButton addButton = new JButton("Add Book");
		JButton deleteButton = new JButton("Delete Book");
		JButton borrowButton = new JButton("Borrow Book");
		JButton returnButton = new JButton("Return Book");
		JButton backButton = new JButton("← Back to Dashboard");

		if (loggedInUser.isAdmin()) {
			buttonPanel.add(addButton);
			buttonPanel.add(deleteButton);
		}

		for (JButton btn : new JButton[]{borrowButton, returnButton, backButton}) {
			buttonPanel.add(btn);
		}

		for (JButton btn : new JButton[]{addButton, deleteButton, borrowButton, returnButton, backButton}) {
			btn.setForeground(new Color(20, 20, 60));
			btn.setFont(new Font("SansSerif", Font.BOLD, 14));
			btn.setBackground(new Color(200, 200, 200));
		}

		addButton.addActionListener(e -> {
			JTextField titleField = new JTextField(15);
			JTextField authorField = new JTextField(15);
			JCheckBox availableBox = new JCheckBox("Available");
			availableBox.setSelected(true);

			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Title:"));
			panel.add(titleField);
			panel.add(new JLabel("Author:"));
			panel.add(authorField);
			panel.add(availableBox);

			int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				String title = titleField.getText().trim();
				String author = authorField.getText().trim();
				boolean available = availableBox.isSelected();

				if (!title.isEmpty() && !author.isEmpty()) {
					BookDAO.addBook(new Book(0, title, author, available));
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Add Book", title);
					loadBooks();
					if (dashboardRef != null) {
						dashboardRef.updateStats();
					}
				}
			}
		});

		deleteButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				int bookId = (int) tableModel.getValueAt(selectedRow, 0);
				String title = (String) tableModel.getValueAt(selectedRow, 1);
				int confirm = JOptionPane.showConfirmDialog(this, "Delete this book?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					BookDAO.deleteBook(bookId);
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Delete Book", title);
					loadBooks();
					if (dashboardRef != null) {
						dashboardRef.updateStats();
					}
				}
			}
		});

		borrowButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}
			String availability = (String) tableModel.getValueAt(selectedRow, 3);
			if ("No".equalsIgnoreCase(availability)) {
				return;
			}

			List<Student> students = StudentDAO.getAllStudents();
			JComboBox<Student> studentComboBox = new JComboBox<>();
			for (Student s : students) {
				studentComboBox.addItem(s);
			}

			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Select Student:"));
			panel.add(studentComboBox);

			int result = JOptionPane.showConfirmDialog(this, panel, "Borrow Book", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				Student selectedStudent = (Student) studentComboBox.getSelectedItem();
				int bookId = (int) tableModel.getValueAt(selectedRow, 0);
				String borrowDate = java.time.LocalDate.now().toString();

				BorrowTransaction transaction = new BorrowTransaction(0, bookId, selectedStudent.getId(), borrowDate, null);
				BorrowTransactionDAO.addTransaction(transaction);
				BookDAO.updateAvailability(bookId, false);
				LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Borrow Book", selectedStudent.getName());
				loadBooks();
				if (dashboardRef != null) {
					dashboardRef.updateStats();
				}
			}
		});

		returnButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}

			int bookId = (int) tableModel.getValueAt(selectedRow, 0);
			List<BorrowTransaction> transactions = BorrowTransactionDAO.getActiveTransactions();
			for (BorrowTransaction t : transactions) {
				if (t.getBookId() == bookId) {
					String returnDate = java.time.LocalDate.now().toString();
					BorrowTransactionDAO.returnBook(t.getId(), returnDate);
					BookDAO.updateAvailability(bookId, true);
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Return Book", String.valueOf(bookId));
					break;
				}
			}
			loadBooks();
			if (dashboardRef != null) {
				dashboardRef.updateStats();
			}
		});

		backButton.addActionListener(e -> {
			dispose();
			if (dashboardRef != null) {
				dashboardRef.setVisible(true);
			}
		});

		contentPanel.add(scrollPane, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPanel);
		loadBooks();
	}

	private void loadBooks() {
		List<Book> books = BookDAO.getAllBooks();
		tableModel.setRowCount(0);
		for (Book book : books) {
			tableModel.addRow(new Object[]{
				book.getId(),
				book.getTitle(),
				book.getAuthor(),
				book.isAvailable() ? "Yes" : "No"
			});
		}
	}

	private void searchBooks() {
		String keyword = searchField.getText().trim().toLowerCase();
		if (keyword.isEmpty()) {
			loadBooks();
			return;
		}

		List<Book> books = BookDAO.getAllBooks();
		tableModel.setRowCount(0);
		for (Book book : books) {
			if (book.getTitle().toLowerCase().contains(keyword)
				|| book.getAuthor().toLowerCase().contains(keyword)) {
				tableModel.addRow(new Object[]{
					book.getId(),
					book.getTitle(),
					book.getAuthor(),
					book.isAvailable() ? "Yes" : "No"
				});
			}
		}
	}
}
