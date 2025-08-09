package view;

import util.AppIconLoader;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {

	public AboutDialog(JFrame parent) {
		super(parent, "About", true);

		AppIconLoader.applyAppIcon(this);
		setSize(400, 250);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());

		JLabel title = new JLabel("📚 Library Management System", JLabel.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 18));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

		JLabel version = new JLabel("Version: 1.0.0", JLabel.CENTER);
		JLabel author = new JLabel("Developed by: Engy Mohamed Ali Hanafy", JLabel.CENTER);
		JLabel contact = new JLabel("Contact: eng.engymohamed33@gmail.com", JLabel.CENTER);
		JLabel date = new JLabel("Release Date: August 2025", JLabel.CENTER);

		JPanel content = new JPanel(new GridLayout(4, 1, 0, 5));
		content.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
		content.add(version);
		content.add(author);
		content.add(contact);
		content.add(date);

		JButton closeBtn = new JButton("Close");
		closeBtn.setPreferredSize(new Dimension(100, 30));
		closeBtn.addActionListener(e -> dispose());

		JPanel footer = new JPanel();
		footer.add(closeBtn);

		add(title, BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
		add(footer, BorderLayout.SOUTH);
	}
}
