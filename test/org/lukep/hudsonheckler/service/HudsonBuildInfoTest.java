package org.lukep.hudsonheckler.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.lukep.hudsonheckler.Configuration;
import org.lukep.hudsonheckler.ResourcePathFinder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HudsonBuildInfoTest {

	private static final String BUILD_PAGE_URL = "buildPageUrl";
	private static final String STATUS_MSG = "project-awesome #20 (aborted)";
	private static final Date FEED_ENTRY_DATE = new GregorianCalendar(2000, 1, 1, 12, 00).getTime();
	
	@Mock private Configuration configuration;
	
	@Mock private ResourcePathFinder resourcePathFinder;
	
	private URL url;
	
	private HudsonBuildInfo hudsonBuildInfo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		when(configuration.getString(HudsonBuildInfo.HUDSON_STATUS_MATCHERS_CONFIG_KEY)).thenReturn("normal,aborted");
		when(configuration.getString(HudsonBuildInfo.HUDSON_ICONS_CONFIG_KEY)).thenReturn("normal.bmp,aborted.bmp");
		when(configuration.getResourcePathFinder()).thenReturn(resourcePathFinder);
		
		url = new URL("http://github.com/");
		when(resourcePathFinder.getPathFor(eq("aborted.bmp"))).thenReturn(url);
		
		hudsonBuildInfo = new HudsonBuildInfo(STATUS_MSG, BUILD_PAGE_URL, FEED_ENTRY_DATE, configuration);
	}

	@Test
	public void testGetProject() {
		assertEquals("project-awesome", hudsonBuildInfo.getProject());
	}

	@Test
	public void testGetBuildId() {
		assertEquals("#20", hudsonBuildInfo.getBuildId());
	}

	@Test
	public void testGetStatus() {
		assertEquals("aborted", hudsonBuildInfo.getStatus());
	}

	@Test
	public void testGetBuildTime() {
		assertEquals(FEED_ENTRY_DATE, hudsonBuildInfo.getBuildTime());
	}

	@Test
	public void testGetLocalIcon() throws MalformedURLException {
		assertEquals(url, hudsonBuildInfo.getLocalIcon());
	}

	@Test
	public void testGetUniqueBuildIdentifier() {
		assertEquals("project-awesome#20", hudsonBuildInfo.getUniqueBuildIdentifier());
	}

	@Test
	public void testGetRevisions() {
		HudsonSourceRevision rev1 = mock(HudsonSourceRevision.class),
				rev2 = mock(HudsonSourceRevision.class);
		hudsonBuildInfo.addApplicableRevision(rev1);
		hudsonBuildInfo.addApplicableRevision(rev2);
		
		Collection<HudsonSourceRevision> revisions = hudsonBuildInfo.getRevisions();
		assertEquals(2, revisions.size());
		assertTrue(revisions.contains(rev1));
		assertTrue(revisions.contains(rev2));
	}
	
	@Test
	public void testIsIncludedInStatusMatchers() {
		assertTrue(hudsonBuildInfo.isIncludedInStatusMatchers());
	}
	
	@Test
	public void testWeirdlyFormattedStatusMessageGracefullyFails() {
		hudsonBuildInfo = new HudsonBuildInfo("something weird", BUILD_PAGE_URL, FEED_ENTRY_DATE, configuration);
		assertEquals(HudsonBuildInfo.UNKNOWN, hudsonBuildInfo.getProject());
		assertEquals(HudsonBuildInfo.UNKNOWN, hudsonBuildInfo.getBuildId());
		assertEquals(HudsonBuildInfo.UNKNOWN, hudsonBuildInfo.getStatus());
	}

}
