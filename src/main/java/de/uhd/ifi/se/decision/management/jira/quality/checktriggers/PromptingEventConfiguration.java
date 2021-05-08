package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import java.util.HashSet;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the just-in-time-prompts of "smart"
 * ConDec features to nudge the developers to use these features for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class PromptingEventConfiguration {
	private Set<String> eventsForLinkSuggestion;
	private Set<String> eventsForDefinitionOfDoneChecking;

	public PromptingEventConfiguration() {
		eventsForLinkSuggestion = new HashSet<>();
		eventsForDefinitionOfDoneChecking = new HashSet<>();
	}

	public boolean isPromptEventForLinkSuggestionActivated(String eventName) {
		return eventsForLinkSuggestion.contains(eventName);
	}

	public void setPromptEventForLinkSuggestion(String eventKey, boolean isActivated) {
		setPromptEvent(eventKey, isActivated, eventsForLinkSuggestion);
	}

	private void setPromptEvent(String eventKey, boolean isActivated, Set<String> activatedEvents) {
		if (isActivated) {
			activatedEvents.add(eventKey);
		} else {
			activatedEvents.remove(eventKey);
		}
	}

	public boolean isPromptEventForDefinitionOfDoneCheckingActivated(String eventName) {
		return eventsForDefinitionOfDoneChecking.contains(eventName);
	}

	public void setPromptEventForDefinitionOfDoneChecking(String eventKey, boolean isActivated) {
		setPromptEvent(eventKey, isActivated, eventsForDefinitionOfDoneChecking);
	}
}
