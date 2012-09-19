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
			ResourcePathFinder resourcePathFinder = new ResourcePathFinder();
			Configuration.getInstance().setResourcePathFinder( resourcePathFinder );
			
			// get a handle on our desired notifier
			DesktopNotifier notifier = DesktopNotifierFactory.getByName(Configuration._getString("notifier"), 
					Configuration._getString("growlHost"), Application.APPLICATION_NAME);
			
			// init application
			Application app = new Application( resourcePathFinder );
			app.setPollInterval(Configuration._getInt("pollIntervalSecs"));
			
			// initialise services with serviceUrls 0-9
			Class<?> serviceClass = Class.forName(Configuration._getString("serviceImpl"));
			for (int i = 0; i < 10; i++) {
				String serviceUrl = Configuration._getString("serviceUrl-"+i);
				if (serviceUrl == null)
					break;
				Service service = (Service) serviceClass.newInstance();
				service.setName(Configuration._getString("serviceName-"+i));
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
