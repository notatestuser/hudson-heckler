package org.lukep.hudsonheckler.notify;

import java.lang.reflect.Constructor;
import java.net.URL;

import org.gnome.gtk.Gtk;
import org.gnome.gtk.Widget;
import org.gnome.notify.Notify;

public class GnomeNotifier extends NonRepeatingDesktopNotifier {

	public GnomeNotifier(String[] gtkArgs, String appName) {
		init(gtkArgs, appName);
	}
	
	private void init(String[] gtkArgs, String appName) {
		Gtk.init(gtkArgs);
		Notify.init(appName);
	}

	@Override
	public boolean isInitialized() {
		return Notify.isInitialized();
	}

	@Override
	protected void displayNotification(Notification n) {
		final String arg0 = n.getTitle(),
					 arg1 = n.getMessage(),
					 arg2 = n.getLocalIcon() != null ? getIconPath(n.getLocalIcon()) : null;
		
		org.gnome.notify.Notification notification = null;
		Constructor<org.gnome.notify.Notification> m = null;
		try {
			// version 4.1.*
			m = org.gnome.notify.Notification.class.getConstructor(String.class, String.class, String.class);
			notification = m.newInstance(arg0, arg1, arg2);
		} catch (Exception e) {
			try {
				// version 4.0.*
				m = org.gnome.notify.Notification.class.getConstructor(String.class, String.class, String.class, Widget.class);
				notification = m.newInstance(arg0, arg1, arg2, null);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
		
		notification.show();
	}
	
	private String getIconPath(URL iconPath) {
		return iconPath.toString().replaceFirst("file\\:", "");
	}

}
