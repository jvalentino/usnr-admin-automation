package com.blogspot.jvalentino.usnrauto.util

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

class DialogUtil {

	static void displayMessages(JDialog view, List<String> errors) {
		if (errors.size() != 0) {
			
			String message = ""
			for (String error : errors) {
				message += error + "\n";
			}
			
			JOptionPane.showMessageDialog(view,
				message,
				"Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}
}
