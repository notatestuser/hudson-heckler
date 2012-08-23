package org.lukep.hudsonheckler;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lukep.hudsonheckler.notify.*;
import org.lukep.hudsonheckler.service.Service;

public class Main {
	
	/**
	 * The program's main entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// get a handle on our desired notifier
			DesktopNotifier notifier = DesktopNotifier.Factory.getByName(Configuration.get("notifier"), 
					Configuration.get("growlHost"), Application.APPLICATION_NAME);
			
			// init application
			Application app = new Application();
			app.setPollInterval(Configuration.getInt("pollIntervalSecs"));
			
			// initialise services with serviceUrls 0-9
			Class<?> serviceClass = Class.forName(Configuration.get("serviceImpl"));
			for (int i = 0; i < 10; i++) {
				String serviceUrl = Configuration.get("serviceUrl-"+i);
				if (serviceUrl == null)
					break;
				Service service = (Service) serviceClass.newInstance();
				service.setName(Configuration.get("serviceName-"+i));
				service.setRootUrl(serviceUrl);
				app.addService(service);
			}
			
			app.addDesktopNotifier(notifier); // must be done after services are added
			app.start();
		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Couldn't start", JOptionPane.ERROR_MESSAGE);
			
			// drop out if we get an error on init...
			System.exit(0);
		}
	}

}
