package util;

import javax.swing.*;
import java.awt.*;

public class AppIconLoader {

	public static void applyAppIcon(JFrame frame) {
		try {
			Image icon = Toolkit.getDefaultToolkit().getImage("assets/logo.png");
			frame.setIconImage(icon);
		} catch (Exception e) {
			System.out.println("Failed to load app icon.");
		}
	}

	public static void applyAppIcon(Window window) {
		try {
			ImageIcon icon = new ImageIcon("assets/logo.png");
			window.setIconImage(icon.getImage());
		} catch (Exception e) {
			System.err.println("Failed to load application icon.");
		}
	}
}
