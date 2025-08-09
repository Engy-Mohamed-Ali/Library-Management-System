package view;

import controller.BorrowTransactionDAO;
import model.BorrowTransaction;
import util.AppIconLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class BorrowHistoryView extends JFrame {

	private JTable table;
	private DefaultTableModel tableModel;
	private BufferedImage backgroundImage;
	private MainDashboard dashboardRef;

	public BorrowHistoryView(MainDashboard dashboardRef) {
		this.dashboardRef = dashboardRef;

		AppIconLoader.applyAppIcon(this);
		setTitle("📖 Borrow History");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);

		tableModel = new DefaultTableModel(new Object[]{"Book Title", "Student Name", "Borrow Date", "Return Date"}, 0);
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

		JButton backButton = new JButton("← Back to Dashboard");
		styleButton(backButton);
		backButton.addActionListener(e -> {
			dispose();
			if (dashboardRef != null) {
				dashboardRef.setVisible(true); 
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(backButton);

		contentPanel.add(scrollPane, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPanel);

		loadHistory();
	}

	private void styleButton(JButton button) {
		button.setForeground(new Color(20, 20, 60));
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setBackground(new Color(200, 200, 200));
	}

	private void loadHistory() {
		tableModel.setRowCount(0);
		List<String[]> history = BorrowTransactionDAO.getTransactionDetails(null, null);
		for (String[] row : history) {
			tableModel.addRow(row);
		}
	}
}
