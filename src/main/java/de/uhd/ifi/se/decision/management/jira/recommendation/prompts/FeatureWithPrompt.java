package de.uhd.ifi.se.decision.management.jira.recommendation.prompts;

/**
 * ConDec features for that just-in-time prompts exists. Just-in-time prompts
 * should nudge the developers to use the feature.
 */
public enum FeatureWithPrompt {
	LINK_RECOMMENDATION, // recommendation of related elements and duplicates
	DOD_CHECKING, // definition of done checking
	DECISION_GUIDANCE, // recommendation from external knowledge sources
	TEXT_CLASSIFICATION; // non-validated elements checking

	public static FeatureWithPrompt getFeatureByName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		for (FeatureWithPrompt feature : values()) {
			if (feature.name().equalsIgnoreCase(name)) {
				return feature;
			}
		}
		return null;
	}
}