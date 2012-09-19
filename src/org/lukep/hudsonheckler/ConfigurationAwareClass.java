package org.lukep.hudsonheckler;

public abstract class ConfigurationAwareClass {
	
	private Configuration configurationSource;
	
	protected ConfigurationAwareClass(Configuration configurationSource) {
		this.configurationSource = configurationSource;
	}
	
	protected Configuration getConfiguration() {
		return configurationSource;
	}
	
	protected ResourcePathFinder getResourcePathFinder() {
		return configurationSource.getResourcePathFinder();
	}

}
