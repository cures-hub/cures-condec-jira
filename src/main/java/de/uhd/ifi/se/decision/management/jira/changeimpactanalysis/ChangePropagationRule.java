package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

/**
 * Represents one propagation rule of a specific
 * {@link ChangePropagationRuleType} for rule-based change impact analysis.
 * 
 * @see ChangePropagationRuleType
 * 
 * @issue How can we model different change propagation rules for CIA?
 * @decision We create both a class ChangePropagationRule and an enum
 *           ChangePropagationRuleType!
 * @pro Enables to deserialize JSON objects passed via the REST API. Seems to be
 *      the only way to enable the rationale manager to configure the change
 *      propagation rules in the project setting view.
 * @con More code than only having the enum.
 * @alternative We used to have only the enum.
 * @con It is not possible (at least we do not how) to deserialize JSON objects
 *      passed via the REST API into enums.
 */
public class ChangePropagationRule {

	private ChangePropagationRuleType type;
	private boolean isActive;
	private float weightValue;

	@JsonCreator
	public ChangePropagationRule(String ruleDescription) {
		this(ChangePropagationRuleType.fromString(ruleDescription));
	}

	@JsonCreator
	public ChangePropagationRule(@JsonProperty("name") String ruleTypeName, @JsonProperty("isActive") Boolean isActive,
			@JsonProperty("weightValue") float weightValue) {
		this.type = ChangePropagationRuleType.valueOf(ruleTypeName);
		this.weightValue = weightValue;
		this.isActive = isActive;
	}

	public ChangePropagationRule(ChangePropagationRuleType ruleType) {
		this.type = ruleType;
		this.weightValue = 1.0f;
		this.isActive = true;
	}

	/**
	 * @return the activation status of the change propagation rule.
	 */
	@XmlElement
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            activation status of the change propagation rule.
	 */
	@JsonProperty("isActive")
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the weight value of the change propagation rule.
	 */
	@XmlElement
	public float getWeightValue() {
		return weightValue;
	}

	/**
	 * @param weightValue
	 *            of the change propagation rule.
	 */
	@JsonProperty
	public void setWeightValue(float weightValue) {
		this.weightValue = weightValue;
	}

	/**
	 * @return {@link ChangePropagationRuleType} of this rule.
	 */
	@XmlElement
	public ChangePropagationRuleType getType() {
		return type;
	}

	/**
	 * @return description of the change propagation rule.
	 */
	@XmlElement
	public String getDescription() {
		return type.getDescription();
	}

	/**
	 * @return explanation of the change propagation rule.
	 */
	@XmlElement
	public String getExplanation() {
		return type.getExplanation();
	}

	/**
	 * @issue How can we make sure that the CIA configuration Velocity page doesn't crash when
	 * 		a CIA rule is deleted and the stored ChangePropagationRule in the database
	 * 		no longer exists?
	 * @decision Check whether any given rule exists by implementing a method that tries
	 * 		to invoke a rule-specific method and utilize it in the Velocity template.
	 * 
	 * @return true if the rule exists, false otherwise.
	 */
	@XmlElement
	public boolean doesRuleExist() {
		try {
			type.getDescription();
            return true;
        }
        catch (Exception e) {
            return false;
        }
	}

	/**
	 * @param ruleWeightValue
	 * 		the weight value of a specific rule.
	 * @param score 
	 * 		the similarity score prior to having weights being applied.
	 * @return score with weights being applied.
	 */
	public static double addWeightValue(float ruleWeightValue, double score) {
		double similarityScore = score;
		double upperScoreBorder = 1.00;
		double lowerScoreBorder = 0.75;
		double result = 0;

		// Reverse effects of rule result for negative weights, i.e. 0.75 -> 1.0 | 1.0 -> 0.75
		// Works best for values between 0.75 and 1.0
		if (ruleWeightValue < 0) {
			similarityScore = Math.pow(2, (-1 * Math.pow(score, 3))) + 0.25;
		}

		// Increase the supplied score if equal or over the arithmetic mean, otherwise reduce it
		if (similarityScore >= (upperScoreBorder + lowerScoreBorder) / 2) {
			result = similarityScore * Math.abs(ruleWeightValue);
		} else {
			result = similarityScore * (2 - Math.abs(ruleWeightValue));
		}

		// Result has to be within the specified borders, otherwise a single rule would
		// impact the score too much
		if (result >= upperScoreBorder) {
			return upperScoreBorder;
		}
		if (result <= lowerScoreBorder) {
			return lowerScoreBorder;
		}

		return result;
	}

	public static List<ChangePropagationRule> getDefaultRules() {
		List<ChangePropagationRule> defaultRules = new LinkedList<>();
		for (ChangePropagationRuleType type : ChangePropagationRuleType.values()) {
			defaultRules.add(new ChangePropagationRule(type));
		}
		return defaultRules;
	}

	public static float getWeightForRule(List<ChangePropagationRule> allRules, ChangePropagationRuleType type) {
		float ruleWeight = allRules.stream().filter(rule -> rule.getType() == type).findAny().get().getWeightValue();
		return ruleWeight;
	}

	public static float getWeightForRule(FilterSettings filterSettings, ChangePropagationRuleType type) {
		return getWeightForRule(filterSettings.getChangeImpactAnalysisConfig().getPropagationRules(), type);
	}

	/**
	 * @return true if the {@link ChangePropagationRuleType} of this and the other
	 *         object is the same.
	 */
	public boolean equals(Object otherRule) {
		return ((ChangePropagationRule) otherRule).getType() == this.getType();
	}
}
