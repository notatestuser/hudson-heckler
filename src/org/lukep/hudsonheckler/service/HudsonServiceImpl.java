package org.lukep.hudsonheckler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.*;

import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.lukep.hudsonheckler.Configuration;
import org.lukep.hudsonheckler.notify.HudsonBuildStatusNotification;
import org.lukep.hudsonheckler.notify.Notification;
import org.lukep.hudsonheckler.notify.NotifyingObservable;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class HudsonServiceImpl implements Service {
	
	private static final String RSS_URI     = "rssLatest";
	private static final String CHANGES_URI = "changes";
	
	private static final String CHANGES_REV_INFO_REGEX = 
			"\\<a\\>([0-9]+)\\<\\/a\\>[\\r\\n ]+by <a href=\\\"\\/user\\/(.+)\\/\\\"";
	
	private static final int MAX_REVISION_CACHE_SIZE = 1024;
	
	private static Logger LOG = Logger.getLogger(Configuration.class.getName());
	
	private NotifyingObservable eventObservable;
	
	private String name = "unset";
	private URL rootUrl = null;
	private URL feedUrl = null;
	
	private Map<String, Date> lastProjectFeedEntry;
	private Map<String, HudsonSourceRevision[]> revisionCache;
	private boolean fetchRevisionInfo = true;
	
	private SyndFeed injectedFeed = null;
	
	public HudsonServiceImpl() {
		this.lastProjectFeedEntry = new HashMap<String, Date>();
		this.revisionCache = new HashMap<String, HudsonSourceRevision[]>();
		if (null == this.eventObservable)
			this.eventObservable = new NotifyingObservable();
	}
	
	public HudsonServiceImpl(SyndFeed feedToInject, NotifyingObservable observable) {
		this();
		eventObservable = observable;
		injectedFeed = feedToInject;
	}
	
	@Override
	public void poll(boolean shouldNotify) throws Exception {
		
		SyndFeed feed = injectedFeed;
		if (null == feed) {
			SyndFeedInput input = new SyndFeedInput();
			
			XmlReader xmlr = new XmlReader(Request.Get(feedUrl.toString())
					.version(HttpVersion.HTTP_1_1)
					.userAgent(HudsonServiceImpl.class.getSimpleName())
					.connectTimeout(Configuration.getInt("connectTimeout"))
					.socketTimeout(Configuration.getInt("socketTimeout"))
					.execute().returnContent().asStream());
			
			feed = input.build(xmlr);
		}
		
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();
		
		Collections.sort(entries, 
				Collections.reverseOrder(new Comparator<SyndEntry>() {
					@Override
					public int compare(SyndEntry o1, SyndEntry o2) {
						if (o1.getUpdatedDate().equals(o2.getUpdatedDate()))
							return 0;
						if (o1.getUpdatedDate().before(o2.getUpdatedDate()))
							return -1;
						return 1;
					}
				}));
		
		// build up a stack of our Notification instances so that we may strip out any that 
		// don't pass shouldShow()
		Stack<HudsonBuildStatusNotification> batch = new Stack<HudsonBuildStatusNotification>();
		HudsonBuildStatusNotification notification = null;
		SyndEntry entry = null;
		for (int i = 0; i < entries.size(); i++) {
			entry = entries.get(i);
			
			HudsonBuildInfo buildInfo = fetchBuildInfo(entry);
			notification = Notification.createHudson(buildInfo, name);
			
			// we're assuming there's one RSS feed entry per project (containing information about its most recent build)
			if (notification.shouldShow() && ! batch.contains(notification)) {
				batch.push(notification);
			}
		}
		
		int shown = 0;
		while ( ! batch.empty()) {
			notification = batch.pop();
			if (notification != null) {
				String projectName = notification.getBuildInfo().getProject();
	    		if ( ! lastProjectFeedEntry.containsKey(projectName)
						|| notification.getEventTime().after(lastProjectFeedEntry.get(projectName))) {
					
					// displaying notifications may be disabled on the first run through
					if (shouldNotify && shown++ < POLL_EVENT_LIMIT) {
			    		eventObservable.hasChanged();
			    		eventObservable.notifyObservers(notification);
					}

					lastProjectFeedEntry.put(projectName, notification.getEventTime());
					
					LOG.info(String.format("Last event for project '%s' in '%s' happened at %s", projectName, 
							name, notification.getEventTime()));
				}
			}
		}
	}

	private HudsonBuildInfo fetchBuildInfo(SyndEntry entry) throws ClientProtocolException, IOException {
		String buildPageUrl = entry.getLink();
		HudsonBuildInfo build = new HudsonBuildInfo(entry.getTitle(), buildPageUrl, entry.getUpdatedDate());
		
		// download the buildPageUrl + 'changes', regex for /user/([a-z0-9])/
		// .. build our URL
		StringBuilder changesPageUrl = new StringBuilder(buildPageUrl);
		if ( ! buildPageUrl.endsWith("/"))
			changesPageUrl.append('/');
		changesPageUrl.append(CHANGES_URI);
		
		if (revisionCache.containsKey(build.getUniqueBuildIdentifier())) {
			build.addApplicableRevisions(revisionCache.get(build.getUniqueBuildIdentifier()));
		
		} else if (fetchRevisionInfo) {
			// .. make the request
			String buildPageContent = Request.Get(changesPageUrl.toString())
					.version(HttpVersion.HTTP_1_1)
					.userAgent(HudsonServiceImpl.class.getSimpleName())
					.connectTimeout(Configuration.getInt("connectTimeout"))
					.socketTimeout(Configuration.getInt("socketTimeout"))
					.execute().returnContent().asString();
			
			// .. and apply our regex to grab the applicable matches
			List<HudsonSourceRevision> revisions = new ArrayList<HudsonSourceRevision>();
			Matcher m = Pattern.compile(CHANGES_REV_INFO_REGEX).matcher(buildPageContent);
			while (m.find()) {
				HudsonSourceRevision hsr = new HudsonSourceRevision(Integer.parseInt(m.group(1)), m.group(2));
				build.addApplicableRevision(hsr);
				revisions.add(hsr);
			}
			
			// .. clear the cache out if it's getting A LITTLE TOO LARGE
			if (revisionCache.size() > MAX_REVISION_CACHE_SIZE) {
				revisionCache.clear();
			}
		
			revisionCache.put(build.getUniqueBuildIdentifier(), revisions.toArray(new HudsonSourceRevision[]{}));
		}
		
		return build;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void setRootUrl(String rootUrl) throws MalformedURLException {
		this.rootUrl = new URL(rootUrl);
		this.feedUrl = new URL(rootUrl + (!rootUrl.endsWith("/") ? "/" : "") + RSS_URI);
	}

	@Override
	public void addEventObserver(Observer o) {
		eventObservable.addObserver(o);
	}

	@Override
	public URL getFullPageUrl(String resource) throws MalformedURLException {
		return rootUrl;
	}
	
	public void setFetchRevisionInfo(boolean fetchRevisionInfo) {
		this.fetchRevisionInfo = fetchRevisionInfo;
	}

	@Override
	public String toString() {
		return String.format("'%s' at %s", name, rootUrl.toString());
	}

	@Override
	public String getName() {
		return name;
	}
	
}
