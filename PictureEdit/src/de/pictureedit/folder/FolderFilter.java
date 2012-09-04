package de.pictureedit.folder;

import java.io.File;
import java.io.FilenameFilter;

public class FolderFilter implements FilenameFilter {

	@Override
	public boolean accept(File arg0, String arg1) {
		return arg1.toLowerCase().endsWith(".jpg") || arg1.toLowerCase().endsWith(".JPG") || arg1.toLowerCase().endsWith(".jpeg") || arg1.toLowerCase().endsWith(".JPEG");
	}

}
