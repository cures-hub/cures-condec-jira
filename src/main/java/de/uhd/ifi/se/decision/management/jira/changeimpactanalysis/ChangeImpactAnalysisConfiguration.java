package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for change impact analysis (CIA) for one
 * Jira project (see {@link DecisionKnowledgeProject}). The change impact is
 * calculated by the {@link ChangeImpactAnalysisService}.
 */
public class ChangeImpactAnalysisConfiguration {

	private float decayValue;
	private float threshold;
	private Map<String, Float> linkImpact;
	private long context;
	private List<ChangePropagationRule> propagationRules;

	@JsonCreator
	public ChangeImpactAnalysisConfiguration() {
		decayValue = 0.25f;
		threshold = 0.25f;
		linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
		context = 0;
		propagationRules = ChangePropagationRule.getDefaultRules();
	}

	public ChangeImpactAnalysisConfiguration(float decayValue, float threshold, long context, List<ChangePropagationRule> propagationRules) {
		this.decayValue = decayValue;
		this.threshold = threshold;
		this.linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
		this.context = context;
		this.propagationRules = propagationRules;
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

	@XmlElement
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

	@XmlElement
	public List<ChangePropagationRule> getPropagationRules() {
		return propagationRules;
	}

	@JsonProperty
	public void setPropagationRules(List<ChangePropagationRule> propagationRules) {
		this.propagationRules = propagationRules;
	}
}