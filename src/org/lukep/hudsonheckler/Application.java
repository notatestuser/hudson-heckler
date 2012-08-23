package org.lukep.hudsonheckler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

import org.lukep.hudsonheckler.notify.*;
import org.lukep.hudsonheckler.service.Service;

public class Application extends NotifyingObservable {
	
	public static final String APPLICATION_NAME = "Hudson Heckler";
	public static final String APPLICATION_RELS = "v0.1";
	private static final String TRAY_ICON_PATH  = "hudson_2_32x32x32.png";

	private Set<Service>       services = new HashSet<Service>();
	private Set<Service>       brokenServices = new HashSet<Service>();
	private int                pollInterval = 60;
	private Timer              pollTimer = null;
	private volatile TimerTask pollTimerTask = null;
	
	public Application() { super(); }
	
	public void addService(Service service) {
		services.add(service);
	}
	
	public void setPollInterval(int seconds) {
		if (seconds <= 0)
			throw new IllegalArgumentException();
		pollInterval = seconds;
	}
	
	public void addDesktopNotifier(DesktopNotifier desktopNotifier) {
		addObserver(desktopNotifier);
		for (Service mService : services) {
			mService.addEventObserver(desktopNotifier);
		}
	}
	
	public void start() throws Exception {
		// pre-load config vars
		Configuration.getInstance();
		
		addTrayIcon();
		
		if (pollTimer != null) {
			pollTimer.cancel();
		}
		
		// show startup message
		StringBuilder sb = new StringBuilder();
		for (Service service : services) {
			sb.append("\n" + service);
		}
		notifyObservers(Notification.create(String.format("%s %s started", APPLICATION_NAME, APPLICATION_RELS), 
				String.format("Polling frequency %d secs, %d max events per poll%s", 
						pollInterval, Service.POLL_EVENT_LIMIT, sb.toString())));

		// begin poll timer
		pollForEvents(Configuration.getBoolean("showInitialEvents"));
		pollTimer = new Timer(false);
		pollTimer.scheduleAtFixedRate((pollTimerTask = new ApplicationTask(this)), 0, pollInterval * 1000);
	}
	
	protected void pollForEvents(boolean shouldNotify) {
		Service currentService = null;
		try {
			for (Service mService : services) {
				(currentService = mService).poll(shouldNotify);
				if (brokenServices.contains(mService)) {
					brokenServices.remove(mService);
					notifyObservers(Notification.create(
							"Crisis averted!", String.format("Regained connectivity to '%s'; new events will be shown as they happen.", 
									mService.getName())));
				}
			}
		} catch (Exception e) {
			brokenServices.add(currentService);
			notifyObservers(Notification.create(
					"Uh oh! Woe is me!", 
					String.format("Something terrible happened whilst trying to talk to '%s': %s", 
							currentService.getName(), e.getMessage())));
		}
	}

	protected void pollForEvents() {
		pollForEvents(true);
	}

	protected void addTrayIcon() {
		if ( ! SystemTray.isSupported()) {
			System.out.println("No system tray support.");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(
				new ImageIcon(ResourcePathFinder.getPathFor(TRAY_ICON_PATH), "tray icon").getImage(), getTooltipText());
		final SystemTray tray = SystemTray.getSystemTray();

		// service items
		for (final Service service : services) {
			MenuItem serviceItem = new MenuItem(String.format("Open %s", service.getName()));
			serviceItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						Desktop.getDesktop().browse(service.getFullPageUrl("").toURI());
					} catch (Exception e) {
						notifyObservers(Notification.create("Can't open the service page!", e.getMessage()));
					}
				}
			});
			popup.add(serviceItem);
		}
		
		// edit configuration
		MenuItem showConfigurationItem = new MenuItem("Edit configuration file");
		showConfigurationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Desktop.getDesktop().open(new File(ResourcePathFinder.getPathFor("notifier.config").toURI()));
					notifyObservers(Notification.create("Opening configuration file", "Please restart the application to apply any changes."));
				} catch (Exception e) {
					notifyObservers(Notification.create("Can't open the file!", e.getMessage()));
				}
			}
		});
		popup.add(showConfigurationItem);
		
		// exit
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shutdown();
			}
		});
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);
		
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			notifyObservers(Notification.create(
					"Error!", "Unable to create tray icon."));
			e.printStackTrace();
		}
	}
	
	protected void shutdown() {
		notifyObservers(Notification.create("Quitting", "Have a nice day!"));
		
		if (pollTimerTask != null) {
			pollTimerTask.cancel();
		}
		if (pollTimer != null) {
			pollTimer.cancel();
			pollTimer.purge();
			pollTimer = null;
		}
		
        // TODO: remove tray icon
        System.exit(0);
	}
	
	private String getTooltipText() {
		return String.format("%s is polling every %d secs", APPLICATION_NAME, pollInterval);
	}
} // class Application
