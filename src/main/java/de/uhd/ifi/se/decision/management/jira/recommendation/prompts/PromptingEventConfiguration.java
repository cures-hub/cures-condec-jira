package de.uhd.ifi.se.decision.management.jira.recommendation.prompts;

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

	private Map<FeatureWithPrompt, Set<String>> promptingEventsForFeature;

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
		for (FeatureWithPrompt feature : FeatureWithPrompt.values()) {
			promptingEventsForFeature.put(feature, allEventNames);
		}
	}

	public boolean isPromptEventActivated(FeatureWithPrompt feature, String eventName) {
		return isValidFeature(feature) && promptingEventsForFeature.get(feature).contains(eventName);
	}

	public boolean isPromptEventActivated(String featureName, String eventName) {
		return isPromptEventActivated(FeatureWithPrompt.getFeatureByName(featureName), eventName);
	}

	public boolean isValidFeature(FeatureWithPrompt feature) {
		if (feature == null) {
			return false;
		}
		if (promptingEventsForFeature == null) {
			promptingEventsForFeature = new HashMap<>();
		}
		if (!promptingEventsForFeature.containsKey(feature)) {
			promptingEventsForFeature.put(feature, new HashSet<>());
		}
		return true;
	}

	public void setPromptEvent(FeatureWithPrompt feature, String eventKey, boolean isActivated) {
		if (!isValidFeature(feature)) {
			return;
		}
		if (isActivated) {
			promptingEventsForFeature.get(feature).add(eventKey);
		} else {
			promptingEventsForFeature.get(feature).remove(eventKey);
		}
	}

	public Map<FeatureWithPrompt, Set<String>> getPromptingEventsForFeature() {
		return promptingEventsForFeature;
	}

	public void setPromptingEventsForFeature(Map<FeatureWithPrompt, Set<String>> promptingEventsForFeature) {
		this.promptingEventsForFeature = promptingEventsForFeature;
	}
}
