package de.pictureedit.folder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ReadFolder {
	
	public static List<String> getPictureFilesInFolder(String path) throws IOException {
		File folder = new File(path);
		if(!folder.exists() || !folder.isDirectory()) {
			throw new IOException("Folder does not exist or folder is not a directory!");
		}
		File[] files = folder.listFiles(new FolderFilter());
		List<String> fileNameList = new ArrayList<String>();
		for(int i = 0; i < files.length; i++) {
			fileNameList.add(files[i].getAbsolutePath());
		}
		return fileNameList;
	}
	
}
