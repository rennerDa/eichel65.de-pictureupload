package de.pictureedit.helper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

public final class PropertyLoader {

	private PropertyLoader() {
		
	}
	
	public static String getProperty(String propertyName) {
		Properties properties = new Properties();
		try {
			BufferedInputStream stream = new BufferedInputStream(ClassLoader.getSystemResourceAsStream("de/pictureedit/helper/configuration.properties"));
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			return "";
		}
		String property = properties.getProperty(propertyName);
		if (property == null) {
			return "";
		}
		return property;
	}
	
}
