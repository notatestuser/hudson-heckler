package org.lukep.hudsonheckler;

import java.util.TimerTask;
import java.util.logging.Logger;

import org.lukep.hudsonheckler.notify.Notification;

public class ApplicationTask extends TimerTask {

	private static final int SYSTEM_TIME_STRAY_TOLERANCE_MS = 5000;
	
	private static Logger LOG = Logger.getLogger(Configuration.class.getName());
	
	private final Application application;
	
	private boolean initialTimerPoll = true;
	private boolean initialPollSucceeded;
	
	private long lastSystemTimeMillis = getCurrentSystemTime();

	/**
	 * @param application
	 * @param initialPollResult 
	 */
	ApplicationTask(Application application, boolean initialPollSucceeded) {
		this.application = application;
		this.initialPollSucceeded = initialPollSucceeded;
	}

	@Override
	public synchronized void run() {
		if (initialTimerPoll) {
			lastSystemTimeMillis = getCurrentSystemTime();
		}
		
		long strayMs = getCurrentSystemTime() - lastSystemTimeMillis;
		boolean shouldNotify = true;
		
		if (initialTimerPoll && ! initialPollSucceeded) {
			LOG.info("Suppressing notifications this round (it's our first timed poll and we haven't yet been successful)");
			shouldNotify = false;
		}
		
		if (strayMs > Configuration._getInt("pollIntervalSecs") * 1000 + SYSTEM_TIME_STRAY_TOLERANCE_MS) {
			int strayMins = (int) Math.ceil((strayMs / 1000) / 60);
			
			shouldNotify = false;
			
			application.notifyObservers(Notification.create(
					"System unsuspended - hiding events", 
					String.format("System is awake after being down for %d minute(s).", strayMins)));
			
			LOG.info(String.format("System unsuspended, suppressing notifications on next update. strayMins = %d", strayMins));
		}
		
		if (application.pollForEvents(shouldNotify)) {
			initialTimerPoll = false;
		}
		
		lastSystemTimeMillis = getCurrentSystemTime();
	}
	
	private long getCurrentSystemTime() {
		return System.currentTimeMillis();
	}
	
}
