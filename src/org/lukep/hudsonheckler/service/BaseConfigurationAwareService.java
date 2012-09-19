package org.lukep.hudsonheckler.service;

import org.lukep.hudsonheckler.Configuration;
import org.lukep.hudsonheckler.ConfigurationAwareClass;

public abstract class BaseConfigurationAwareService extends ConfigurationAwareClass implements Service {
	
	public static final String MAX_POLL_EVENTS_CONFIG_KEY = "maxEventsPerPoll";

	protected BaseConfigurationAwareService(Configuration configurationSource) {
		super(configurationSource);
	}

	protected int getPollEventLimit() {
		return getConfiguration().getInt(MAX_POLL_EVENTS_CONFIG_KEY);
	}

}
