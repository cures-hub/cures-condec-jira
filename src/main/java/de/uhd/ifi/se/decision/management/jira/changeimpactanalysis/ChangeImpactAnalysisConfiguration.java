package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private List<PassRule> passRule;

	public ChangeImpactAnalysisConfiguration() {
		decayValue = 0.75f;
		threshold = 0.25f;
		linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getAllNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
		context = 0;
		this.passRule = new LinkedList<>();
	}

	public float getDecayValue() {
		return decayValue;
	}

	public void setDecayValue(Float decayValue) {
		this.decayValue = decayValue;
	}

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

	public List<PassRule> getPropagationRules() {
		return passRule;
	}

	public void setPropagationRules(List<String> rules) {
		if (rules == null) {
			passRule.clear();
			return;
		}
		passRule.clear();
		for (String stringRule : rules) {
			passRule.add(PassRule.getPropagationRule(stringRule));
		}
	}
}