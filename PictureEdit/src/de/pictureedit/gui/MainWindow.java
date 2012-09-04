package de.pictureedit.gui;

import java.awt.Component;
import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import de.pictureedit.fileupload.FTPFileUpload;
import de.pictureedit.folder.ReadFolder;
import de.pictureedit.gui.listener.ChooseFolderListener;
import de.pictureedit.gui.listener.EditPictureListener;
import de.pictureedit.gui.listener.TxtStateChangeListener;
import de.pictureedit.httprequest.HTTPRequest;
import de.pictureedit.picture.Picture;

public class MainWindow extends JFrame {

	private static MainWindow instance;

	public static MainWindow getInstance() {
		if (MainWindow.instance == null) {
			MainWindow.instance = new MainWindow();
		}
		return MainWindow.instance;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9078310044195819949L;

	private JPanel contentPane;

	private String picturePath;

	private JLabel lblPictures_1;

	private JButton btnStartConverting;

	private JProgressBar progressBar;
	private JProgressBar progressBar_1;

	private JLabel lblWorkingOnPicture;
	private JLabel lblWorkingOnPicture_1;

	private List<String> pictureList;

	private JLabel lblPath;

	private JTextField txtPhotograph;
	private JTextField txtName;
	private JTextField txtDate;

	private EditPictureListener listener = null;

	public void setPicturePath(String pp) {
		this.picturePath = pp;
		lblPath.setText(pp);
	}

	public void changeButtonState() {
		if (this.picturePath == null) {
			btnStartConverting.setEnabled(false);
			btnStartConverting.removeMouseListener(this.listener);
		} else {
			try {
				this.pictureList = ReadFolder
						.getPictureFilesInFolder(this.picturePath);
			} catch (IOException e) {
				MainWindow.showErrorMessage(this, "Converting error", "An error occured during picture convertion.");
			}
			this.lblPictures_1.setText(this.pictureList.size() + " pictures");
			this.lblWorkingOnPicture.setText("Working on picture 0 of "
					+ this.pictureList.size());
			lblWorkingOnPicture_1.setText("Working on picture 0 of "
					+ pictureList.size());

			if (this.pictureList.size() > 0 && !txtName.getText().equals("")
					&& !txtDate.getText().equals("")
					&& !txtPhotograph.getText().equals("")) {
				btnStartConverting.setEnabled(true);
				this.listener = new EditPictureListener();
				btnStartConverting.addMouseListener(this.listener);
			} else {
				btnStartConverting.setEnabled(false);
				btnStartConverting.removeMouseListener(this.listener);
			}
		}
	}

	public void editPictures() {

		String seperator = "/";
		if (this.picturePath.contains("\\")) {
			seperator = "\\";
		}

		final String tempFilePath = this.picturePath + seperator
				+ "tempPictures" + seperator;
		File createFolder = new File(tempFilePath);
		createFolder.mkdir();

		this.progressBar.setMaximum(this.pictureList.size());
		this.progressBar.setMinimum(0);
		this.progressBar.setValue(0);
		
		this.progressBar_1.setMaximum(this.pictureList.size());
		this.progressBar_1.setMinimum(0);
		this.progressBar_1.setValue(0);
		
		Thread t = new Thread() {
			public void run() {
				int i = 1;
				for (String picturePath : pictureList) {
					lblWorkingOnPicture.setText("Working on picture " + i
							+ " of " + pictureList.size());
					progressBar.setValue(i++);
					progressBar.validate();
					progressBar.repaint();
					lblWorkingOnPicture.validate();
					lblWorkingOnPicture.repaint();

					Picture pic = new Picture(txtName.getText(),
							txtPhotograph.getText(), txtDate.getText(),
							picturePath, tempFilePath);
					pic.editPicture();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				String serverPath = (txtDate.getText() + "_" + txtName.getText() + "-" + txtPhotograph
						.getText()).replace(' ', '_');
				uploadPictures(pictureList, serverPath, tempFilePath);
				try {
					HTTPRequest.sendHTTPRequest(serverPath, txtPhotograph.getText(), txtName.getText(), txtDate.getText());
				} catch (Exception e) {
					MainWindow.showErrorMessage(MainWindow.this, "HTTP Request Error", "An error occured during HTTP-Request.");
				}
				JOptionPane.showMessageDialog(MainWindow.getInstance(), "Your pictures were successfully resized and uploaded!");
			}
		};
		t.start();
	}

	public void uploadPictures(List<String> pictures, String galleryName,
			String tempFilePath) {

		List<String> pictureNames = new ArrayList<String>();
		for (String picture : pictures) {
			int position = 0;
			if (picture.lastIndexOf("\\") == -1) {
				position = picture.lastIndexOf("/");
			} else {
				position = picture.lastIndexOf("\\");
			}
			pictureNames.add(picture.substring(position + 1));
		}

		FTPFileUpload ftpUpload = new FTPFileUpload();
		ftpUpload.uploadFiles(pictureNames, galleryName, tempFilePath);
	}

	public void updateUploadProgressBar(int i) {
		lblWorkingOnPicture_1.setText("Uploading picture " + String.valueOf(i)
				+ " of " + pictureList.size());
		lblWorkingOnPicture_1.validate();
		lblWorkingOnPicture_1.repaint();

		progressBar_1.setValue(i);
		progressBar_1.validate();
		progressBar_1.repaint();
	}
	
	public static void showErrorMessage(Component parentComponent, String header, String message) {
		JOptionPane.showMessageDialog(parentComponent,
			    message,
			    header,
			    JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setBackground(SystemColor.window);
		setResizable(false);
		setTitle("Picture-Converter v1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 455, 507);

		contentPane = new JPanel();
		// contentPane.setBorder(BorderFactory.createTitledBorder("TEST"));
		this.setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Input"));

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(BorderFactory.createTitledBorder("Output"));

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																panel,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																428,
																Short.MAX_VALUE)
														.addComponent(
																panel_1,
																Alignment.LEADING,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(11, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 224,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 213,
								Short.MAX_VALUE).addContainerGap()));

		JLabel lblPictures = new JLabel("Pictures:");

		lblPictures_1 = new JLabel("- Pictures");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		lblWorkingOnPicture = new JLabel("Working on picture X of X");
		lblWorkingOnPicture.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel lblUpload = new JLabel("Upload:");

		progressBar_1 = new JProgressBar();
		progressBar_1.setStringPainted(true);

		lblWorkingOnPicture_1 = new JLabel("Uploading picture X of X");
		lblWorkingOnPicture_1.setHorizontalAlignment(SwingConstants.CENTER);

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1
				.setHorizontalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																Alignment.TRAILING,
																gl_panel_1
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				gl_panel_1
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addGroup(
																								Alignment.LEADING,
																								gl_panel_1
																										.createSequentialGroup()
																										.addComponent(
																												lblPictures)
																										.addGap(118)
																										.addComponent(
																												lblPictures_1))
																						.addComponent(
																								progressBar,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								396,
																								Short.MAX_VALUE)
																						.addComponent(
																								lblWorkingOnPicture,
																								GroupLayout.DEFAULT_SIZE,
																								396,
																								Short.MAX_VALUE)
																						.addComponent(
																								lblUpload,
																								Alignment.LEADING)
																						.addComponent(
																								progressBar_1,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								396,
																								Short.MAX_VALUE)
																						.addComponent(
																								lblWorkingOnPicture_1,
																								GroupLayout.DEFAULT_SIZE,
																								396,
																								Short.MAX_VALUE))))
										.addContainerGap()));
		gl_panel_1
				.setVerticalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblPictures)
														.addComponent(
																lblPictures_1))
										.addGap(19)
										.addComponent(progressBar,
												GroupLayout.PREFERRED_SIZE, 22,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(lblWorkingOnPicture)
										.addGap(18)
										.addComponent(lblUpload)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(progressBar_1,
												GroupLayout.PREFERRED_SIZE, 23,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(lblWorkingOnPicture_1)
										.addGap(18)));
		panel_1.setLayout(gl_panel_1);

		JLabel lblPhotograph = new JLabel("Photograph:");

		JLabel lblFolderOfPictures = new JLabel("Folder of pictures:");

		btnStartConverting = new JButton("Start converting & upload data");
		btnStartConverting.setEnabled(false);

		lblPath = new JLabel("Path");

		lblPath.setText("No Path actually set");

		JLabel lblPleaseInsertThe = new JLabel(
				"Please insert albumname, photograph and date of the album:");
		// test.setIconsTheme(JDirectoryChooser.ICONS_LOOK_AND_FEEL);

		txtPhotograph = new JTextField();
		txtPhotograph.setColumns(1);

		JButton btnChooseTheFolder = new JButton("Choose Folder");
		btnChooseTheFolder.addMouseListener(new ChooseFolderListener());

		JLabel lblAlbumname = new JLabel("Albumname:");

		txtName = new JTextField();
		txtName.setColumns(10);

		JLabel lblDate = new JLabel("Date:");

		txtDate = new JTextField();
		txtDate.setColumns(10);

		txtName.addCaretListener(new TxtStateChangeListener());
		txtDate.addCaretListener(new TxtStateChangeListener());
		txtPhotograph.addCaretListener(new TxtStateChangeListener());

		JLabel lblFe = new JLabel("f. e. 2012-12-31");

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(
														lblPleaseInsertThe)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(27)
																								.addComponent(
																										btnChooseTheFolder))
																				.addComponent(
																						lblAlbumname)
																				.addComponent(
																						lblPhotograph))
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.TRAILING)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(1)
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.LEADING)
																												.addComponent(
																														txtPhotograph,
																														GroupLayout.DEFAULT_SIZE,
																														267,
																														Short.MAX_VALUE)
																												.addComponent(
																														txtName,
																														237,
																														237,
																														237)
																												.addComponent(
																														lblPath,
																														GroupLayout.DEFAULT_SIZE,
																														267,
																														Short.MAX_VALUE)
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addComponent(
																																		txtDate,
																																		GroupLayout.PREFERRED_SIZE,
																																		159,
																																		GroupLayout.PREFERRED_SIZE)
																																.addPreferredGap(
																																		ComponentPlacement.RELATED)
																																.addComponent(
																																		lblFe))))
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addComponent(
																										btnStartConverting)
																								.addGap(26))))
												.addComponent(
														lblFolderOfPictures)
												.addComponent(lblDate))
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(5)
																.addComponent(
																		lblPleaseInsertThe)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		lblAlbumname)
																.addGap(13)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						lblPhotograph)
																				.addComponent(
																						txtPhotograph,
																						GroupLayout.PREFERRED_SIZE,
																						GroupLayout.DEFAULT_SIZE,
																						GroupLayout.PREFERRED_SIZE)))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(26)
																.addComponent(
																		txtName,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(lblDate)
												.addGroup(
														gl_panel.createParallelGroup(
																Alignment.BASELINE)
																.addComponent(
																		txtDate,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		lblFe)))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblPath)
												.addComponent(
														lblFolderOfPictures))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														btnChooseTheFolder)
												.addComponent(
														btnStartConverting))
								.addContainerGap()));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);

	}
}
