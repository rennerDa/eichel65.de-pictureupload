package de.pictureedit;

import java.awt.EventQueue;

import javax.swing.UIManager;

import de.pictureedit.gui.MainWindow;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = MainWindow.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
		ReadFolder folder = null;
		try {
			folder = new ReadFolder("/home/daniel/workspace_picture/");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		folder.getJPEGFilesInFolder();
		
		Picture pic = new Picture();
		pic.editPicture();*/
	}

}
