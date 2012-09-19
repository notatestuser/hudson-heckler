package org.lukep.hudsonheckler.notify;

import java.net.URL;
import java.util.Date;

import org.lukep.hudsonheckler.service.HudsonBuildInfo;

public class Notification {
	
	protected String title;
	protected String message;
	protected Date eventTime;
	protected URL localIcon;
	
	public Notification(String title, String message, Date eventTime, URL localIconUrl) {
		super();
		this.title = title;
		this.message = message;
		this.eventTime = eventTime;
		this.localIcon = localIconUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}
	
	public Date getEventTime() {
		return eventTime;
	}

	public URL getLocalIcon() {
		return localIcon;
	}
	
	public boolean shouldShow() {
		return true;
	}
	
	public static Notification create(String title, String message) {
		return new Notification(title, message, new Date(), null);
	}
	
	public static Notification create(String title, String message, Date eventTime) {
		return new Notification(title, message, eventTime, null);
	}
	
	public static Notification create(String title, String message, Date eventTime, URL localIconUrl) {
		return new Notification(title, title, eventTime, localIconUrl);
	}
	
	public static HudsonBuildStatusNotification createHudson(HudsonBuildInfo buildInfo, String serviceName, String dateFormatStr) {
		return new HudsonBuildStatusNotification(buildInfo, serviceName, dateFormatStr);
	}
	
	// hashCode() and equals()
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
}
