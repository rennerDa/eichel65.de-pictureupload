package de.pictureedit.gui.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import com.jtechlabs.ui.widget.directorychooser.JDirectoryChooser;

import de.pictureedit.gui.MainWindow;

public class ChooseFolderListener implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) {
		JDirectoryChooser test = new JDirectoryChooser();
		File initialDir = new File(System.getProperty("user.home"));
		File dir = JDirectoryChooser.showDialog(test, initialDir, "Choose directory", "Select picture directory:");
		MainWindow mw = MainWindow.getInstance();
		if (dir != null && dir.isDirectory()) {
			mw.setPicturePath(dir.getAbsolutePath());
			mw.changeButtonState();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
