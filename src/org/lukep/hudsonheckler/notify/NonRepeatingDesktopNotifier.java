package org.lukep.hudsonheckler.notify;

import java.util.Observable;
import java.util.logging.Logger;

import org.lukep.hudsonheckler.Configuration;

public abstract class NonRepeatingDesktopNotifier implements DesktopNotifier {

	private static Logger LOG = Logger.getLogger(Configuration.class.getName());
	
	private Notification lastNotification;
	
	@Override
	public void update(Observable arg0, Object arg1) {
		offerNotification((org.lukep.hudsonheckler.notify.Notification) arg1);
	}

	@Override
	public void offerNotification(Notification notification) {
		if (lastNotification == null || ! lastNotification.equals(notification)) {
			LOG.info(String.format("Displaying notification [title = %s]", notification.getTitle()));
			displayNotification(notification);
		} else {
			LOG.info(String.format("Suppressed a duplicate notification [title = %s]", notification.getTitle()));
		}
		lastNotification = notification;
	}
	
	protected abstract void displayNotification(Notification notification);

}
