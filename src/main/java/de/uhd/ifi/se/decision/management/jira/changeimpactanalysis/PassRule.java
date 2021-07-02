package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

public enum PassRule {

	IGNORE_ARGUMENTS("IgnoreArgumentsFromAlternatives", "IgnoreArgumentsFromAlternatives",
			new IgnoreArgumentsDecisionsPassRule()), //
	IGNORE_DecisionIncoming("IgnoreDecisionIncoming", "IgnoreDecisionIncoming", new IgnoreDecisionIncoming());

	private String description;
	private String translation;
	private PassPredicate predicate;

	PassRule(String description, String translation, PassPredicate predicate) {
		this.description = description;
		this.translation = translation;
		this.predicate = predicate;
	}

	public String getDescription() {
		return description;
	}

	public String getTranslation() {
		return translation;
	}

	public PassPredicate getPredicate() {
		return predicate;
	}

	public static PassRule getPropagationRule(String status) {
		if (status == null || status.isEmpty()) {
			return null;
		}
		for (PassRule passRule : PassRule.values()) {
			if (passRule.getTranslation().matches(status)) {
				return passRule;
			}
		}
		return null;
	}
}
