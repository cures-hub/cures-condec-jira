package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for change impact analysis (CIA) for one
 * Jira project (see {@link DecisionKnowledgeProject}).
 */
public class ChangeImpactAnalysisConfiguration {

	private float decayValue;
	private float threshold;
	private Map<String, Float> linkImpact;
	private long context;
	private List<PassRule> passRules;

	@JsonCreator
	public ChangeImpactAnalysisConfiguration() {
		decayValue = 0.75f;
		threshold = 0.25f;
		linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getAllNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
		context = 0;
		passRules = List.of(PassRule.values());
	}

	@XmlElement
	public float getDecayValue() {
		return decayValue;
	}

	public void setDecayValue(Float decayValue) {
		this.decayValue = decayValue;
	}

	@XmlElement
	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(Float threshold) {
		this.threshold = threshold;
	}

	public Map<String, Float> getLinkImpact() {
		return linkImpact;
	}

	public void setLinkImpact(Map<String, Float> linkImpact) {
		this.linkImpact = linkImpact;
	}

	public long getContext() {
		return context;
	}

	public void setContext(long context) {
		this.context = context;
	}

	@JsonIgnore
	public List<PassRule> getPropagationRules() {
		return passRules;
	}

	@XmlElement(name = "propagationRules")
	public List<String> getPropagationRulesAsStrings() {
		return passRules.stream().map(PassRule::getTranslation).filter(entry -> !entry.equals("undefined"))
				.collect(Collectors.toList());
	}

	@JsonProperty
	public void setPropagationRules(List<String> rules) {
		if (rules == null) {
			passRules.clear();
			return;
		}
		passRules.clear();
		for (String stringRule : rules) {
			passRules.add(PassRule.getPropagationRule(stringRule));
		}
	}
}