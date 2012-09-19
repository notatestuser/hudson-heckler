package org.lukep.hudsonheckler.notify;

import java.util.Observer;


public interface DesktopNotifier extends Observer {

	public boolean isInitialized();
	
	public void offerNotification(Notification notification);
	
}
