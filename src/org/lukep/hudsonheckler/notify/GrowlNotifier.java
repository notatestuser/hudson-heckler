package org.lukep.hudsonheckler.notify;

import net.sf.libgrowl.Application;
import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.IResponse;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;

public class GrowlNotifier extends NonRepeatingDesktopNotifier {

	private GrowlConnector growlConnector;
	private Application growlApp;
	
	private NotificationType growlEvent;
	
	public GrowlNotifier(String growlHost, String applicationName) {
		growlConnector = new GrowlConnector(growlHost);
		growlApp = new net.sf.libgrowl.Application(applicationName);
		
		// create the Growl notification types
		growlEvent = new NotificationType("New event");
		if (growlConnector.register(growlApp, new NotificationType[] { growlEvent }) != IResponse.OK) {
			throw new IllegalStateException("Couldn't connect to Growl. Is it running?");
		}
	}
	
	@Override
	public boolean isInitialized() {
		return growlConnector != null && growlApp != null;
	}

	@Override
	protected void displayNotification(org.lukep.hudsonheckler.notify.Notification notification) {
		Notification growlNotification = new Notification(growlApp, growlEvent, sanitise(notification.getTitle()), 
				sanitise(notification.getMessage()));
		growlConnector.notify(growlNotification);
	}

	private String sanitise(String title) {
		return title.replaceAll("\\n", " ");
	}

}
