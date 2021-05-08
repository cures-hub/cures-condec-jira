package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public interface QualityCheckEventTrigger {

	boolean isTriggered();

	String getCurrentProjectKey();

	void setIssueEvent(IssueEvent event);

	default boolean isActivated() {
		PromptingEventConfiguration config = ConfigPersistenceManager
				.getPromptingEventConfiguration(getCurrentProjectKey());
		return config.isPromptEventForLinkSuggestionActivated(getName());
	}

	String getName();
}
