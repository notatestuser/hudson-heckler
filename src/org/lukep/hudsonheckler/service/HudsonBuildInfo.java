package org.lukep.hudsonheckler.service;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lukep.hudsonheckler.Configuration;
import org.lukep.hudsonheckler.ResourcePathFinder;

public class HudsonBuildInfo {
	
	private static final String PATTERN = "^([a-z0-9._-]+)\\ ([#0-9]+)\\ ([0-9]\\ )?\\((.+)\\)$";
	
	private static final String HUDSON_STATUS_MATCHERS_CONFIG_KEY = "hudsonStatusMatchers";
	private static final String HUDSON_ICONS_CONFIG_KEY = "hudsonStatusIcons";
	
	private String project, buildId, status;
	private Date buildTime;
	private URL localIcon;
	private boolean includedInStatusMatchers = false;
	private Set<HudsonSourceRevision> revisions = new HashSet<HudsonSourceRevision>(5);

	HudsonBuildInfo(String statusMsg, String buildPageUrl, Date buildTime) {
		this.buildTime = buildTime;
		parseBuildAttributes(statusMsg);
	}
	
	private void parseBuildAttributes(String title) {
		// parse our entry title
		Matcher p = Pattern.compile(PATTERN).matcher(title);
		if (p.matches() && p.groupCount() >= 4) {
			project = p.group(1);
			buildId = p.group(2);
			status = p.group(4);
		} else {
			project = "unable to parse entry title";
			buildId = "?";
			status = "?";
		}
		
		// determine whether we care to show this one;
		// we do this here so that we can find the icon index...
		String[] matchers = Configuration.get(HUDSON_STATUS_MATCHERS_CONFIG_KEY).split("\\,"),
		         icons = Configuration.get(HUDSON_ICONS_CONFIG_KEY).split("\\,");
		
		if (icons.length < matchers.length)
			throw new RuntimeException("The number of icons can't be less than the number of matchers.");
		
		for (int i = 0; i < matchers.length; i++) {
			if (status.contains(matchers[i])) {
				includedInStatusMatchers = true;
				localIcon = ResourcePathFinder.getPathFor(icons[i]);
				break;
			}
		}
	}
	
	public void addApplicableRevision(HudsonSourceRevision hudsonSourceRevision) {
		revisions.add(hudsonSourceRevision);
	}
	
	public void addApplicableRevisions(HudsonSourceRevision[] hudsonSourceRevisions) {
		revisions.addAll(Arrays.asList(hudsonSourceRevisions));
	}
	
	public String getProject() {
		return project;
	}

	public String getBuildId() {
		return buildId;
	}

	public String getStatus() {
		return status;
	}

	public Date getBuildTime() {
		return buildTime;
	}

	public URL getLocalIcon() {
		return localIcon;
	}
	
	public String getUniqueBuildIdentifier() {
		return getProject()+getBuildId();
	}

	public Set<HudsonSourceRevision> getRevisions() {
		return revisions;
	}

	public boolean isIncludedInStatusMatchers() {
		return includedInStatusMatchers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildId == null) ? 0 : buildId.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		HudsonBuildInfo other = (HudsonBuildInfo) obj;
		if (buildId == null) {
			if (other.buildId != null)
				return false;
		} else if (!buildId.equals(other.buildId))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

}
