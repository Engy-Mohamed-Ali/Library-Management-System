package view;

import controller.StudentDAO;
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

public class StudentListView extends JFrame {

	private JTable table;
	private DefaultTableModel tableModel;
	private BufferedImage backgroundImage;
	private JTextField searchField;
	private JButton searchButton;
	private JButton resetButton;
	private MainDashboard dashboardRef;
	private User loggedInUser;

	public StudentListView(MainDashboard dashboardRef, User user) {
		this.dashboardRef = dashboardRef;
		this.loggedInUser = user;

		util.AppIconLoader.applyAppIcon(this);
		setTitle("Students Management");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			backgroundImage = ImageIO.read(new File("assets/background.png"));
		} catch (Exception e) {
		}

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
		resetButton = new JButton("Reset");

		styleButton(searchButton);
		styleButton(resetButton);

		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		searchPanel.add(resetButton);
		contentPanel.add(searchPanel, BorderLayout.NORTH);

		searchButton.addActionListener(e -> searchStudents());
		resetButton.addActionListener(e -> loadStudents());

		tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0);
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

		JButton addButton = new JButton("Add Student");
		JButton deleteButton = new JButton("Delete Student");
		JButton editButton = new JButton("Edit Student");
		JButton backButton = new JButton("← Back to Dashboard");

		styleButton(addButton);
		styleButton(deleteButton);
		styleButton(editButton);
		styleButton(backButton);

		if (loggedInUser.isAdmin()) {
			buttonPanel.add(addButton);
			buttonPanel.add(deleteButton);
		}

		buttonPanel.add(editButton);
		buttonPanel.add(backButton);

		addButton.addActionListener(e -> {
			JTextField nameField = new JTextField(15);
			JTextField emailField = new JTextField(15);

			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Name:"));
			panel.add(nameField);
			panel.add(new JLabel("Email:"));
			panel.add(emailField);

			int result = JOptionPane.showConfirmDialog(this, panel, "Add New Student",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				String name = nameField.getText().trim();
				String email = emailField.getText().trim();

				if (!name.isEmpty() && !email.isEmpty()) {
					StudentDAO.addStudent(new Student(0, name, email));
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Add Student", name);
					loadStudents();
					if (dashboardRef != null) {
						dashboardRef.updateStats();
					}
				}
			}
		});

		deleteButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				int studentId = (int) tableModel.getValueAt(selectedRow, 0);
				String studentName = (String) tableModel.getValueAt(selectedRow, 1);
				int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					StudentDAO.deleteStudent(studentId);
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Delete Student", studentName);
					loadStudents();
					if (dashboardRef != null) {
						dashboardRef.updateStats();
					}
				}
			}
		});

		editButton.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}

			int studentId = (int) tableModel.getValueAt(selectedRow, 0);
			String currentName = (String) tableModel.getValueAt(selectedRow, 1);
			String currentEmail = (String) tableModel.getValueAt(selectedRow, 2);

			JTextField nameField = new JTextField(currentName, 15);
			JTextField emailField = new JTextField(currentEmail, 15);

			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Name:"));
			panel.add(nameField);
			panel.add(new JLabel("Email:"));
			panel.add(emailField);

			int result = JOptionPane.showConfirmDialog(this, panel, "Edit Student",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				String newName = nameField.getText().trim();
				String newEmail = emailField.getText().trim();

				if (!newName.isEmpty() && !newEmail.isEmpty()) {
					Student updated = new Student(studentId, newName, newEmail);
					StudentDAO.updateStudent(updated);
					LoggerUtil.log(loggedInUser.getUsername(), loggedInUser.getRole(), "Edit Student", newName);
					loadStudents();
					if (dashboardRef != null) {
						dashboardRef.updateStats();
					}
				}
			}
		});

		backButton.addActionListener(e -> {
			dispose();
			if (dashboardRef != null) {
				dashboardRef.setVisible(true);
				dashboardRef.updateStats();
			}
		});

		contentPanel.add(scrollPane, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		userInfoPanel.setOpaque(false);

		JLabel userLabel = new JLabel("👤 Logged in as: " + loggedInUser.getUsername() + " [" + loggedInUser.getRole().toUpperCase() + "]");
		userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		userLabel.setForeground(new Color(20, 20, 60));
		userInfoPanel.add(userLabel);
		add(userInfoPanel, BorderLayout.NORTH);

		add(contentPanel);
		loadStudents();
	}

	private void styleButton(JButton button) {
		button.setForeground(new Color(20, 20, 60));
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setBackground(new Color(200, 200, 200));
	}

	private void loadStudents() {
		List<Student> students = StudentDAO.getAllStudents();
		tableModel.setRowCount(0);
		for (Student student : students) {
			tableModel.addRow(new Object[]{
				student.getId(),
				student.getName(),
				student.getEmail()
			});
		}
	}

	private void searchStudents() {
		String keyword = searchField.getText().trim().toLowerCase();
		if (keyword.isEmpty()) {
			loadStudents();
			return;
		}

		List<Student> students = StudentDAO.getAllStudents();
		tableModel.setRowCount(0);
		for (Student student : students) {
			if (student.getName().toLowerCase().contains(keyword)
				|| student.getEmail().toLowerCase().contains(keyword)) {
				tableModel.addRow(new Object[]{
					student.getId(),
					student.getName(),
					student.getEmail()
				});
			}
		}
	}
}
