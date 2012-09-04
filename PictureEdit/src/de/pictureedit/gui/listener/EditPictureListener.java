package de.pictureedit.gui.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.pictureedit.gui.MainWindow;

public class EditPictureListener implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			MainWindow.getInstance().editPictures();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
