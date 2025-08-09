package view;

import util.AppIconLoader;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;

public class LogViewer extends JFrame {

	private JTextPane logPane;
	private StyledDocument doc;

	public LogViewer() {
		AppIconLoader.applyAppIcon(this);

		setTitle("📄 Operation Logs");
		setSize(750, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		logPane = new JTextPane();
		logPane.setEditable(false);
		logPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
		logPane.setBackground(new Color(245, 245, 245));
		doc = logPane.getStyledDocument();

		JScrollPane scrollPane = new JScrollPane(logPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		add(scrollPane, BorderLayout.CENTER);

		JButton refreshButton = new JButton("🔄 Refresh Logs");
		styleButton(refreshButton);
		refreshButton.addActionListener(e -> loadLogs());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setBackground(new Color(240, 240, 240));
		bottomPanel.add(refreshButton);

		add(bottomPanel, BorderLayout.SOUTH);

		loadLogs();
	}

	private void styleButton(JButton button) {
		button.setForeground(new Color(20, 20, 60));
		button.setFont(new Font("SansSerif", Font.BOLD, 13));
		button.setBackground(new Color(220, 220, 220));
	}

	private void loadLogs() {
		doc = new DefaultStyledDocument();
		logPane.setStyledDocument(doc);

		File logFile = new File("logs/operations.log");
		if (!logFile.exists()) {
			appendStyled("No logs found.\n", Color.GRAY);
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				appendStyled(line + "\n", getColorForLine(line));
			}
		} catch (IOException e) {
			appendStyled("Error reading log file.\n", Color.RED);
		}
	}

	private void appendStyled(String text, Color color) {
		SimpleAttributeSet style = new SimpleAttributeSet();
		StyleConstants.setForeground(style, color);
		try {
			doc.insertString(doc.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private Color getColorForLine(String line) {
		line = line.toLowerCase();
		if (line.contains("add")) {
			return new Color(0, 128, 0);
		}
		if (line.contains("delete")) {
			return new Color(178, 34, 34);
		}
		if (line.contains("edit")) {
			return new Color(218, 165, 32);
		}
		if (line.contains("login")) {
			return new Color(70, 130, 180);
		}
		if (line.contains("borrow")) {
			return new Color(128, 0, 128);
		}
		if (line.contains("return")) {
			return new Color(0, 100, 0);
		}
		if (line.contains("export")) {
			return new Color(139, 69, 19);
		}
		return Color.DARK_GRAY;
	}
}
