package org.lukep.hudsonheckler.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observer;

public interface Service {
	
	public void setRootUrl(String rootUrl) throws MalformedURLException;

	public void setName(String name);
	
	public String getName();
	
	public void poll(boolean shouldNotify) throws Exception;
	
	public void addEventObserver(Observer o);
	
	public URL getFullPageUrl(String resource) throws MalformedURLException;

}
