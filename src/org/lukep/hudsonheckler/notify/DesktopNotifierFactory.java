package org.lukep.hudsonheckler.notify;

import javax.swing.UIManager;

public class DesktopNotifierFactory {
	
	public static DesktopNotifier getByName(String shortName,
			String notifierHost, String applicationName) {
		
		if (shortName.equals("growl")) {
			// TODO the line below causes a crash on Ubuntu oneiric amd64 when the GnomeNotifier is used?
			// P.S. I love hacky code hacky code is good
			try {
				// this will make windows look like windows and mac os look like mac os
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// do we care? sure don't!
			}
			
			return new GrowlNotifier(notifierHost, applicationName);
		}
		
		if (shortName.equals("gnome") || shortName.equals("gtk"))
			return new GnomeNotifier(new String[]{}, applicationName);
		
		return null;
	}
	
}
