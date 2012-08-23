package org.lukep.hudsonheckler;

import java.util.TimerTask;

public class ApplicationTask extends TimerTask {

	private final Application application;

	/**
	 * @param application
	 */
	ApplicationTask(Application application) {
		this.application = application;
	}

	@Override
	public void run() {
		this.application.pollForEvents();
	}
	
}
