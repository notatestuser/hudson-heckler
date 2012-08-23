package org.lukep.hudsonheckler.notify;

import java.util.Observable;

public class NotifyingObservable extends Observable {

	public Boolean notifyObservers(Notification notification) {
		setChanged();
		super.notifyObservers((Object) notification);
		return true;
	}
	
}
