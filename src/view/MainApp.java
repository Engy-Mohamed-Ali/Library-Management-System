package view;

import controller.UserDAO;

import javax.swing.SwingUtilities;

public class MainApp {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			UserDAO.fixPasswordsIfNeeded();
			new LoginView().setVisible(true);
		});
	}
}
