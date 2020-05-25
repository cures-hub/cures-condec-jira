package de.uhd.ifi.se.decision.management.jira.eventlistener;

public interface Observabel {
	void register(Subscriber subscriber);
	void unregister(Subscriber subscriber);

}
