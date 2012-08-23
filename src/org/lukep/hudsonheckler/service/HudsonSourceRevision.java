package org.lukep.hudsonheckler.service;

public class HudsonSourceRevision {

	private int revisionNumber;
	private String userName;
	
	HudsonSourceRevision(int revisionNumber, String userName) {
		this.revisionNumber = revisionNumber;
		this.userName = userName;
	}

	public int getRevisionNumber() {
		return revisionNumber;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + revisionNumber;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		HudsonSourceRevision other = (HudsonSourceRevision) obj;
		if (revisionNumber != other.revisionNumber)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	
}
