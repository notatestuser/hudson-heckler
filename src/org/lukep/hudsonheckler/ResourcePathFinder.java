package org.lukep.hudsonheckler;

import java.net.URL;

public class ResourcePathFinder {
	
	public static URL getPathFor(String filename) {
		return Thread.currentThread().getContextClassLoader().getResource(filename);
	}

}
