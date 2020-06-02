package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public interface ConsistencyCheckEventTrigger {

	boolean isTriggered(IssueEvent issueEvent);

	default boolean isActivated(String projectKey){
		return ConfigPersistenceManager.getActivationStatusOfConsistencyEvent(projectKey, this.getName());
	}

	String getName();
}
