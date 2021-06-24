package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the just-in-time-prompts of "smart"
 * ConDec features to nudge the developers to use these features for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class PromptingEventConfiguration {
	private Map<String, HashSet<String>> promptingEventsForFeature;

	public PromptingEventConfiguration() {
		promptingEventsForFeature = new HashMap<>();
		promptingEventsForFeature.put("linkRecommendation", new HashSet<>());
		promptingEventsForFeature.put("definitionOfDoneChecking", new HashSet<>());
		promptingEventsForFeature.put("nonValidatedElementsChecking", new HashSet<>());
	}

	public boolean isPromptEventActivated(String feature, String eventName) {
		return promptingEventsForFeature.get(feature).contains(eventName);
	}

	public boolean isValidFeature(String feature) {
		return promptingEventsForFeature.get(feature) != null;
	}

	public void setPromptEvent(String feature, String eventKey, boolean isActivated) {
		if (!isValidFeature(feature)) {
			return;
		}
		if (isActivated) {
			promptingEventsForFeature.get(feature).add(eventKey);
		} else {
			promptingEventsForFeature.get(feature).remove(eventKey);
		}
	}
}
