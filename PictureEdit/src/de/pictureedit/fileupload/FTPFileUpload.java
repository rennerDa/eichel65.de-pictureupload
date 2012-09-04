package de.pictureedit.fileupload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

import de.pictureedit.gui.MainWindow;
import de.pictureedit.helper.PropertyLoader;

public final class FTPFileUpload {

	public void uploadFiles(List<String> files, String newFolderName,
			String tempFilePath) {
		boolean hidden = false;
		boolean useEpsvWithIPv4 = true;
		long keepAliveTimeout = -1;
		int controlKeepAliveReplyTimeout = -1;

		String server = PropertyLoader.getProperty("ftpServer");
		Integer port = Integer.valueOf(PropertyLoader.getProperty("ftpPort"));
		String username = PropertyLoader.getProperty("ftpUsername");
		String password = PropertyLoader.getProperty("ftpPassword");
		
		final FTPClient ftp = new FTPClient();

		if (keepAliveTimeout >= 0) {
			ftp.setControlKeepAliveTimeout(keepAliveTimeout);
		}
		if (controlKeepAliveReplyTimeout >= 0) {
			ftp.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeout);
		}
		ftp.setListHiddenFiles(hidden);

		// suppress login details
//		ftp.addProtocolCommandListener(new PrintCommandListener(
//				new PrintWriter(System.out), true));

		try {
			int reply;
			if (port > 0) {
				ftp.connect(server, port);
			} else {
				ftp.connect(server);
			}
			System.out.println("Connected to " + server + " on "
					+ (port > 0 ? port : ftp.getDefaultPort()));

			// After connection attempt, you should check the reply code to
			// verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
		} catch (IOException e) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
			MainWindow.showErrorMessage(MainWindow.getInstance(), "Upload error", "An error occured during uploading the pictures.");
			System.exit(1);
		}

		__main: try {
			if (!ftp.login(username, password)) {
				ftp.logout();
				break __main;
			}

			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			// Use passive mode as default because most of us are
			// behind firewalls these days.
			ftp.enterLocalPassiveMode();

			ftp.setUseEPSVwithIPv4(useEpsvWithIPv4);

			if (ftp.isConnected() && ftp.isAvailable()
					&& ftp.changeWorkingDirectory("httpdocs")
					&& ftp.changeWorkingDirectory("picture")
					&& ftp.makeDirectory(newFolderName)
					&& ftp.changeWorkingDirectory(newFolderName)) {
				int i = 1;
				for (String file: files) {
					InputStream input;
					input = new FileInputStream(tempFilePath + file);
					ftp.storeFile(file, input);
					input.close();
					MainWindow.getInstance().updateUploadProgressBar(i++);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				
			}

			ftp.noop(); // check that control connection is working OK

			ftp.logout();
		} catch (FTPConnectionClosedException e) {
			MainWindow.showErrorMessage(MainWindow.getInstance(), "Server error", "The server closed the connection.");
		} catch (IOException e) {
			MainWindow.showErrorMessage(MainWindow.getInstance(), "IOError", "An IOError occured during upload.");
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) {
					// do nothing
				}
			}
		}
	}
}
