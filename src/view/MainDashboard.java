package view;

import model.User;
import controller.BookDAO;
import controller.StudentDAO;
import controller.BorrowTransactionDAO;
import controller.UserDAO;
import util.AppIconLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainDashboard extends JFrame {

	private BufferedImage backgroundImage;
	private JLabel statsLabel;
	private User loggedInUser;

	public MainDashboard(User user) {
		this.loggedInUser = user;

		AppIconLoader.applyAppIcon(this);
		setTitle("Library Management Dashboard");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			backgroundImage = ImageIO.read(new File("assets/background.png"));
		} catch (Exception e) {
			System.out.println("Failed to load background image.");
		}

		JLabel userInfo = new JLabel("👤 Logged in as: " + loggedInUser.getUsername()
			+ " [" + loggedInUser.getRole().toUpperCase() + "]");
		userInfo.setFont(new Font("SansSerif", Font.PLAIN, 14));
		userInfo.setForeground(new Color(20, 20, 60));
		userInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(userInfo, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (backgroundImage != null) {
					g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
				}
			}
		};

		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 15, 15, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JPanel statsPanel = createStatsPanel();
		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPanel.add(statsPanel, gbc);

		JButton booksButton = new JButton("📚 Manage Books");
		JButton studentsButton = new JButton("🎓 Manage Students");
		JButton historyButton = new JButton("📖 View Borrow History");
		JButton changePasswordButton = new JButton("🔑 Change Password");
		JButton profileButton = new JButton("👤 My Profile");
		JButton viewLogsButton = new JButton("📄 View Logs");
		JButton logoutButton = new JButton("🚪 Logout");
		JButton aboutButton = new JButton("❓ About");

		JButton[] mainButtons = {
			booksButton, studentsButton, historyButton,
			changePasswordButton, profileButton
		};

		int row = 1;
		for (JButton btn : mainButtons) {
			styleButton(btn);
			gbc.gridy = row++;
			contentPanel.add(btn, gbc);
		}

		if (!loggedInUser.isAdmin()) {
			studentsButton.setEnabled(false);
			studentsButton.setText("🔒 Students (Admin Only)");
		} else {
			studentsButton.addActionListener(e -> {
				new StudentListView(this, loggedInUser).setVisible(true);
				this.setVisible(false);
			});

			JPanel adminPanel = new JPanel(new GridLayout(0, 1, 10, 10));
			adminPanel.setOpaque(false);
			styleButton(viewLogsButton);
			adminPanel.add(viewLogsButton);
			gbc.gridy = row++;
			contentPanel.add(adminPanel, gbc);
		}

		styleSecondaryButton(logoutButton);
		styleSecondaryButton(aboutButton);

		JPanel bottomPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		bottomPanel.setOpaque(false);
		bottomPanel.add(logoutButton);
		bottomPanel.add(aboutButton);

		gbc.gridy = row++;
		contentPanel.add(bottomPanel, gbc);

		booksButton.addActionListener(e -> {
			new BookListView(this, loggedInUser).setVisible(true);
			this.setVisible(false);
		});

		historyButton.addActionListener(e -> {
			new BorrowHistoryView(this).setVisible(true);
			this.setVisible(false);
		});

		profileButton.addActionListener(e -> {
			dispose();
			new ProfileView(this, loggedInUser).setVisible(true);
		});

		viewLogsButton.addActionListener(e -> new LogViewer().setVisible(true));

		changePasswordButton.addActionListener(e -> {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			JPasswordField oldPass = new JPasswordField();
			JPasswordField newPass = new JPasswordField();
			JPasswordField confirmPass = new JPasswordField();

			panel.add(new JLabel("Old Password:"));
			panel.add(oldPass);
			panel.add(new JLabel("New Password:"));
			panel.add(newPass);
			panel.add(new JLabel("Confirm New Password:"));
			panel.add(confirmPass);

			int result = JOptionPane.showConfirmDialog(this, panel, "Change Password",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				String old = new String(oldPass.getPassword());
				String newP = new String(newPass.getPassword());
				String confirm = new String(confirmPass.getPassword());

				if (!newP.equals(confirm)) {
					JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				boolean success = UserDAO.changePassword(loggedInUser.getUsername(), old, newP);
				if (success) {
					JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Incorrect old password.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		logoutButton.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
				"Confirm Logout", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				this.dispose();
				new LoginView().setVisible(true);
			}
		});

		aboutButton.addActionListener(e -> new AboutDialog(this).setVisible(true));

		add(contentPanel, BorderLayout.CENTER);
	}

	private void styleButton(JButton button) {
		button.setPreferredSize(new Dimension(250, 40));
		button.setFont(new Font("SansSerif", Font.BOLD, 16));
		button.setForeground(new Color(20, 20, 60));
		button.setBackground(new Color(220, 220, 220));
	}

	private void styleSecondaryButton(JButton button) {
		button.setPreferredSize(new Dimension(250, 40));
		button.setFont(new Font("SansSerif", Font.BOLD, 16));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(70, 70, 70));
	}

	private JPanel createStatsPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		statsLabel = new JLabel();
		statsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		statsLabel.setForeground(Color.WHITE);

		updateStats();
		panel.add(statsLabel);
		return panel;
	}

	public void updateStats() {
		int bookCount = BookDAO.getAllBooks().size();
		int studentCount = StudentDAO.getAllStudents().size();
		int activeBorrows = BorrowTransactionDAO.getActiveTransactions().size();

		statsLabel.setText("📚 Books: " + bookCount
			+ "    🎓 Students: " + studentCount
			+ "    🔄 Borrowed: " + activeBorrows);
	}
}
