package org.lukep.hudsonheckler.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.lukep.hudsonheckler.Configuration;
import org.lukep.hudsonheckler.ResourcePathFinder;
import org.lukep.hudsonheckler.notify.HudsonBuildStatusNotification;
import org.lukep.hudsonheckler.notify.Notification;
import org.lukep.hudsonheckler.notify.NotifyingObservable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

// TODO mock the Configuration provider in order to have tests work independently of application config files
public class HudsonServiceImplTest {
	
	@Mock private NotifyingObservable mockedObservable;
	
	@Mock private SyndFeed mockedFeed;
	
	@Mock private Configuration configuration;
	
	@Mock private ResourcePathFinder resourcePathFinder;
	
	private List<SyndEntry> mockedFeedEntries = new ArrayList<SyndEntry>();
	
	private HudsonServiceImpl hudsonService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		for (int i = 0; i < 10; i++) { 
			SyndEntry entry = mock(SyndEntry.class);
			when(entry.getTitle()).thenReturn("project #"+(i+1)+" (unmatched)");
			when(entry.getLink()).thenReturn("http://hudson/"+(i+1));
			when(entry.getUpdatedDate()).thenReturn(
					new Date(new Date().getTime() + 10000 - (i*100)));
			mockedFeedEntries.add(entry);
		}
		when(mockedFeed.getEntries()).thenReturn(mockedFeedEntries);
		
		when(configuration.getString(HudsonServiceImpl.HUDSON_STATUS_DATE_FORMAT_CONFIG_KEY)).thenReturn("E d/M h:ma");
		when(configuration.getString(HudsonBuildInfo.HUDSON_STATUS_MATCHERS_CONFIG_KEY)).thenReturn("normal,aborted,broke");
		when(configuration.getString(HudsonBuildInfo.HUDSON_ICONS_CONFIG_KEY)).thenReturn("n.b,a.b,b.b");
		when(configuration.getInt(HudsonServiceImpl.MAX_POLL_EVENTS_CONFIG_KEY)).thenReturn(5);
		when(configuration.getResourcePathFinder()).thenReturn(resourcePathFinder);
		
		when(resourcePathFinder.getPathFor(anyString())).thenReturn( new URL("http://github.com/") );
				
		hudsonService = new HudsonServiceImpl(mockedFeed, mockedObservable, configuration);
		hudsonService.setName("hudson");
		hudsonService.setRootUrl("http://localhost/");
		hudsonService.setFetchRevisionInfo(false);
	}
	
	@Test
	public void testNoNotificationsToBeShown() throws Exception {
		Collections.shuffle(mockedFeedEntries);
		hudsonService.poll(true);
		verify(mockedObservable, times(0)).notifyObservers(isA(Notification.class));
	}
	
	@Test
	public void testSelectiveNotificationOrderedByUpdatedDate() throws Exception {
		when(mockedFeedEntries.get(0).getTitle()).thenReturn("project #1 (broken)");
		when(mockedFeedEntries.get(1).getTitle()).thenReturn("project #2 (broken)");
		when(mockedFeedEntries.get(6).getTitle()).thenReturn("project #7 (broken)");
		Collections.shuffle(mockedFeedEntries);
		
		final List<Notification> encounteredNotifications = new ArrayList<Notification>();
		when(mockedObservable.notifyObservers(isA(HudsonBuildStatusNotification.class)))
			.thenAnswer(new Answer<Boolean>() {
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					Notification n = (Notification) invocation.getArguments()[0];
					return encounteredNotifications.add(n);
				}
			});
		
		hudsonService.poll(true);
		
		assertEquals(3, encounteredNotifications.size());
		
		assertTrue(encounteredNotifications.get(0).getTitle().equals("project is broken"));
		assertTrue(encounteredNotifications.get(0).getMessage().contains("#7"));
		
		assertTrue(encounteredNotifications.get(1).getTitle().equals("project is broken"));
		assertTrue(encounteredNotifications.get(1).getMessage().contains("#2"));
		
		assertTrue(encounteredNotifications.get(2).getTitle().equals("project is broken"));
		assertTrue(encounteredNotifications.get(2).getMessage().contains("#1"));
	}
	
	@Test
	public void testNoNotificationsAreRepeated() throws Exception {
		when(mockedFeedEntries.get(3).getTitle()).thenReturn("project #4 (broken)");
		when(mockedFeedEntries.get(6).getTitle()).thenReturn("project #7 (broken)");
		
		// poll une (show 2 notifications)
		hudsonService.poll(true);
		verify(mockedObservable, times(2)).notifyObservers(isA(Notification.class));
		
		// poll deux (show no notifications)
		hudsonService.poll(true);
		verify(mockedObservable, times(2)).notifyObservers(isA(Notification.class));
		
		// poll trois (show 2 new notifications)
		Date updated = new Date();
		updated.setTime(new Date().getTime() + 10000);
		when(mockedFeedEntries.get(8).getTitle()).thenReturn("project #20 (aborted)");
		when(mockedFeedEntries.get(8).getUpdatedDate()).thenReturn(updated);
		
		updated = new Date();
		updated.setTime(new Date().getTime() + 100000);
		when(mockedFeedEntries.get(9).getTitle()).thenReturn("project #21 (aborted)");
		when(mockedFeedEntries.get(9).getUpdatedDate()).thenReturn(updated);
		
		final List<Notification> encounteredNotifications = new ArrayList<Notification>();
		when(mockedObservable.notifyObservers(isA(HudsonBuildStatusNotification.class)))
			.thenAnswer(new Answer<Boolean>() {
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					Notification n = (Notification) invocation.getArguments()[0];
					return encounteredNotifications.add(n);
				}
			});
		hudsonService.poll(true);
		verify(mockedObservable, times(4)).notifyObservers(isA(Notification.class));
		
		assertEquals(2, encounteredNotifications.size());
		
		assertEquals("project was aborted", encounteredNotifications.get(0).getTitle());
		assertTrue(encounteredNotifications.get(0).getMessage().contains("#20"));
		
		assertEquals("project was aborted", encounteredNotifications.get(1).getTitle());
		assertTrue(encounteredNotifications.get(1).getMessage().contains("#21"));
	}

}
