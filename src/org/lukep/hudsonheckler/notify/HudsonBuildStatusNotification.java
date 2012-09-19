package org.lukep.hudsonheckler.notify;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lukep.hudsonheckler.service.HudsonBuildInfo;
import org.lukep.hudsonheckler.service.HudsonSourceRevision;

public class HudsonBuildStatusNotification extends Notification {
	
	private HudsonBuildInfo buildInfo;
	
	public HudsonBuildStatusNotification(HudsonBuildInfo buildInfo, String serviceName, String dateFormatStr) {
		super("", "", buildInfo.getBuildTime(), null);
		this.buildInfo = buildInfo;
		this.message = formatMessage(buildInfo.getBuildTime(), serviceName, dateFormatStr);
	}
	
	private String formatMessage(Date publishDate, String serviceName, String dateFormatStr) {
		StringBuilder sb = new StringBuilder();
		// TODO use injected Configuration object to get the hudsonStatusDateFormat?
		sb.append(new SimpleDateFormat(dateFormatStr).format(publishDate));
		if (buildInfo.getRevisions().size() > 0) {
			sb.append("\nChanges in "+serviceName+" by ");
			for (HudsonSourceRevision rev : buildInfo.getRevisions()) {
				sb.append(rev.getUserName() + " ");
			}
		}
		return sb.toString();
	}
	
	public HudsonBuildInfo getBuildInfo() {
		return buildInfo;
	}

	@Override
	public String getTitle() {
		final String status = buildInfo.getStatus();
		if (status.contains("aborted"))
			return String.format("%s was %s", buildInfo.getProject(), status);
		else
			return String.format("%s is %s", buildInfo.getProject(), status);
	}

	@Override
	public String getMessage() {
		return String.format("Build %s at %s", buildInfo.getBuildId(), super.getMessage());
	}
	
	@Override
	public URL getLocalIcon() {
		return buildInfo.getLocalIcon();
	}

	@Override
	public boolean shouldShow() {
		return buildInfo.isIncludedInStatusMatchers();
	}

	@Override
	public String toString() {
		return getTitle() + "\n" + getMessage();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((buildInfo == null) ? 0 : buildInfo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HudsonBuildStatusNotification other = (HudsonBuildStatusNotification) obj;
		if (buildInfo == null) {
			if (other.buildInfo != null)
				return false;
		} else if (!buildInfo.equals(other.buildInfo))
			return false;
		return true;
	}

}
