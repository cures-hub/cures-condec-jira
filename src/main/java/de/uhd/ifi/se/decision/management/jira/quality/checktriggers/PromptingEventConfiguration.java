package de.uhd.ifi.se.decision.management.jira.quality.checktriggers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the just-in-time-prompts of "smart"
 * ConDec features to nudge the developers to use these features for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class PromptingEventConfiguration {
	private Map<String, Set<String>> promptingEventsForFeature;

	/**
	 * @issue Should the prompting events be activated or deactivated per default?
	 * @decision Activate prompting events per default for opt-out nudging!
	 * @pro The developers are nudged/triggered to use the smart ConDec features.
	 * 
	 * @param projectKey
	 *            of a Jira project.
	 */
	public PromptingEventConfiguration(String projectKey) {
		promptingEventsForFeature = new HashMap<>();

		Set<String> allEventNames = JiraSchemeManager.getWorkflowActionNames(projectKey);
		promptingEventsForFeature.put("linkRecommendation", allEventNames);
		promptingEventsForFeature.put("definitionOfDoneChecking", allEventNames);
		promptingEventsForFeature.put("decisionGuidance", allEventNames);
		promptingEventsForFeature.put("nonValidatedElementsChecking", allEventNames);
	}

	public boolean isPromptEventActivated(String feature, String eventName) {
		return isValidFeature(feature) && promptingEventsForFeature.get(feature).contains(eventName);
	}

	public boolean isValidFeature(String feature) {
		if (promptingEventsForFeature.get(feature) != null) {
			return true;
		}
		if (feature == null) {
			return false;
		}
		if (feature.equals("linkRecommendation") || feature.equals("decisionGuidance")
				|| feature.equals("definitionOfDoneChecking") || feature.equals("nonValidatedElementsChecking")) {
			promptingEventsForFeature.put(feature, new HashSet<>());
			return true;
		}
		return false;
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
