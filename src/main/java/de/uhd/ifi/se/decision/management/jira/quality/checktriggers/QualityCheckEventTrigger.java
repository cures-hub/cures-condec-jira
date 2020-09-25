package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public interface QualityCheckEventTrigger {

	boolean isTriggered();

	String getCurrentProjectKey();

	void setIssueEvent(IssueEvent event);

	default boolean isActivated(){
		return ConfigPersistenceManager.getActivationStatusOfQualityEvent(getCurrentProjectKey(), this.getName());
	}

	String getName();
}
