package de.pictureedit.gui.listener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import de.pictureedit.gui.MainWindow;

public class TxtStateChangeListener implements CaretListener{

	@Override
	public void caretUpdate(CaretEvent e) {
		MainWindow.getInstance().changeButtonState();
	}

}
