package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public interface ConsistencyCheckEventTrigger {

	boolean isTriggered();

	String getCurrentProjectKey();

	void setIssueEvent(IssueEvent event);

	default boolean isActivated(){
		return ConfigPersistenceManager.getActivationStatusOfConsistencyEvent(getCurrentProjectKey(), this.getName());
	}

	String getName();
}
