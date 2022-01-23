package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents one propagation rule of a specific
 * {@link ChangePropagationRuleType} for rule-based change impact analysis.
 * 
 * @see ChangePropagationRuleType
 */
public class ChangePropagationRule {

	private ChangePropagationRuleType type;
	private boolean isActive;
	private float weightValue;

	@JsonCreator
	public ChangePropagationRule(@JsonProperty("name") String typeName) {
		this(ChangePropagationRuleType.valueOf(typeName));
	}

	public ChangePropagationRule(ChangePropagationRuleType type) {
		this.type = type;
		this.weightValue = 1.0f;
		this.isActive = false;
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

	public static Set<ChangePropagationRule> getDefaultRules() {
		Set<ChangePropagationRule> defaultRules = new HashSet<>();
		for (ChangePropagationRuleType type : ChangePropagationRuleType.values()) {
			defaultRules.add(new ChangePropagationRule(type));
		}
		return defaultRules;
	}
}
