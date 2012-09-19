package org.lukep.hudsonheckler;

import java.net.URL;

public class ResourcePathFinder {
	
	public URL getPathFor(String filename) {
		return Thread.currentThread().getContextClassLoader().getResource(filename);
	}

}
