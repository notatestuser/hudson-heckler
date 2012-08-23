package org.lukep.hudsonheckler.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observer;

import org.lukep.hudsonheckler.Configuration;


public interface Service {
	
	static final int POLL_EVENT_LIMIT = Configuration.getInt("maxEventsPerPoll");
	
	public void setRootUrl(String rootUrl) throws MalformedURLException;

	public void setName(String name);
	
	public String getName();
	
	public void addEventObserver(Observer o);
	
	public void poll(boolean shouldNotify) throws Exception;
	
	public URL getFullPageUrl(String resource) throws MalformedURLException;

}
