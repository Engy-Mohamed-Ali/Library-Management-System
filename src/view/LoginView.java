package view;

import controller.UserDAO;
import model.Session;
import model.User;
import util.AppIconLoader;
import util.LoggerUtil;
import util.LoginAttemptTracker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class LoginView extends JFrame {

	private JTextField usernameField;
	private JPasswordField passwordField;
	private BufferedImage backgroundImage;

	public LoginView() {
		AppIconLoader.applyAppIcon(this);
		setTitle("Login - Library System");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			backgroundImage = ImageIO.read(new File("assets/background.png"));
		} catch (Exception e) {
			System.out.println("Failed to load background image.");
		}

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
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel titleLabel = new JLabel("Library Login");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		contentPanel.add(titleLabel, gbc);

		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;

		JLabel userLabel = new JLabel("Username:");
		userLabel.setForeground(Color.WHITE);
		gbc.gridy = 1;
		gbc.gridx = 0;
		contentPanel.add(userLabel, gbc);

		usernameField = new JTextField(15);
		gbc.gridx = 1;
		contentPanel.add(usernameField, gbc);

		JLabel passLabel = new JLabel("Password:");
		passLabel.setForeground(Color.WHITE);
		gbc.gridy = 2;
		gbc.gridx = 0;
		contentPanel.add(passLabel, gbc);

		passwordField = new JPasswordField(15);
		gbc.gridx = 1;
		contentPanel.add(passwordField, gbc);

		JButton loginButton = new JButton("Login");
		loginButton.setForeground(new Color(20, 20, 60));
		loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		loginButton.setBackground(new Color(200, 200, 200));
		gbc.gridy = 3;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		contentPanel.add(loginButton, gbc);

		loginButton.addActionListener(e -> performLogin());
		usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
		passwordField.addActionListener(e -> performLogin());

		add(contentPanel, BorderLayout.CENTER);
	}

	private void performLogin() {
		String username = usernameField.getText().trim();
		String password = new String(passwordField.getPassword()).trim();

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (LoginAttemptTracker.isBlocked(username)) {
			JOptionPane.showMessageDialog(this,
				"Too many failed login attempts.\nPlease try again after 10 minutes.",
				"Login Blocked", JOptionPane.WARNING_MESSAGE);
			return;
		}

		User user = UserDAO.login(username, password);
		if (user != null) {
			Session.setCurrentUser(user);
			LoginAttemptTracker.resetAttempts(username);
			LoggerUtil.log(user.getUsername(), user.getRole(), "Login", "Successful login");

			String welcomeMsg = "Welcome " + user.getUsername().toUpperCase() + "!\n"
				+ "Login time: " + java.time.LocalDateTime.now().withNano(0);
			JOptionPane.showMessageDialog(this, welcomeMsg, "Login Successful", JOptionPane.INFORMATION_MESSAGE);

			this.dispose();
			new MainDashboard(user).setVisible(true);
		} else {
			LoginAttemptTracker.recordFailedAttempt(username);
			JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("Failed to set look and feel.");
			}
			new LoginView().setVisible(true);
		});
	}
}
