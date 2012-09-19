package org.lukep.hudsonheckler;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Configuration {
	
	private static final String	 DEFAULT_FILE = "notifier.config";
	private static final Charset FILE_CHARSET = Charsets.UTF_8;
	
	private static Logger LOG = Logger.getLogger(Configuration.class.getName());
	
	private static Configuration instance = null;
	
	private Map<String, String> configuration = new HashMap<String, String>();
	
	private ResourcePathFinder resourcePathFinder;
	
	private Configuration() throws Exception {
		loadConfiguration();
	}
	
	public static Configuration getInstance() throws Exception {
		if (null == instance) {
			instance = new Configuration();
		}
		return instance;
	}
	
	private void loadConfiguration() throws Exception {
		try {
			List<String> lines = Files.readLines(new File(getResourcePathFinder().getPathFor(DEFAULT_FILE).toURI()), 
					FILE_CHARSET);
			
			String[] words = null;
			String key, value;
			for (String line : lines) {
				if (line.startsWith("#"))
					continue;
				
				words = line.split(" ");
				key = trim(words[0]);
				
				StringBuilder valuesb = new StringBuilder(line.length() - key.length());
				for (int i = 1; i < words.length; i++) {
					if (i > 1)
						valuesb.append(' ');
					valuesb.append(trim(words[i]));
				}
				if (valuesb.length() > 0) {
					value = valuesb.toString();
					configuration.put(key, value);
					LOG.log(Level.CONFIG, key+" = "+value);
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Couldn't read application configuration file " + DEFAULT_FILE, e);
			throw e;
		}
	}
	
	public ResourcePathFinder getResourcePathFinder() {
		return resourcePathFinder;
	}

	void setResourcePathFinder(ResourcePathFinder resourcePathFinder) {
		this.resourcePathFinder = resourcePathFinder;
	}

	private String trim(String input) {
		return input.replaceAll("[:=]$", "").trim();
	}
	
	public String getString(String key) {
		return configuration.get(key);
	}
	
	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}
	
	public int getInt(String key) {
		return Integer.parseInt(getString(key));
	}
	
	public static String _getString(String key) {
		try {
			return getInstance().getString(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean _getBoolean(String key) {
		try {
			return getInstance().getBoolean(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int _getInt(String key) {
		try {
			return getInstance().getInt(key);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
