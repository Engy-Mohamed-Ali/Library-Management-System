package view;

import model.User;
import util.AppIconLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ProfileView extends JFrame {

	private User user;
	private Image backgroundImage;
	private MainDashboard dashboardRef;

	public ProfileView(MainDashboard dashboardRef, User user) {
		this.dashboardRef = dashboardRef;
		this.user = user;

		AppIconLoader.applyAppIcon(this);
		setTitle("👤 My Profile");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		setAlwaysOnTop(true);

		try {
			backgroundImage = ImageIO.read(new File("assets/background.png"));
		} catch (Exception e) {
			System.out.println("Failed to load background image.");
		}

		if (dashboardRef != null) {
			dashboardRef.setVisible(false); 
		}

		JPanel mainPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (backgroundImage != null) {
					g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
				}
			}
		};
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;

		String[] labels = {
			"👤 Username: " + user.getUsername(),
			"🆔 ID: " + user.getId(),
			"🛡️ Role: " + user.getRole(),
			"⏰ Login Time: " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
		};

		gbc.gridx = 0;
		for (int i = 0; i < labels.length; i++) {
			gbc.gridy = i;
			JLabel label = new JLabel(labels[i]);
			label.setFont(new Font("SansSerif", Font.BOLD, 14));
			label.setForeground(Color.WHITE);
			mainPanel.add(label, gbc);
		}

		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		closeButton.setBackground(new Color(220, 220, 220));
		closeButton.setForeground(new Color(20, 20, 60));

		closeButton.addActionListener(e -> {
			dispose(); 
			if (dashboardRef != null) {
				dashboardRef.setVisible(true);
			}
		});

		gbc.gridy = labels.length;
		mainPanel.add(closeButton, gbc);

		add(mainPanel, BorderLayout.CENTER);
	}
}
