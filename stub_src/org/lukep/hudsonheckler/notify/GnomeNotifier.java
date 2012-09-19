package org.lukep.hudsonheckler.notify;

import java.net.URL;

public class GnomeNotifier extends NonRepeatingDesktopNotifier {

	public GnomeNotifier(String[] gtkArgs, String appName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isInitialized() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void displayNotification(Notification n) {
		throw new UnsupportedOperationException();
	}
	
	private String getIconPath(URL iconPath) {
		throw new UnsupportedOperationException();
	}

}
