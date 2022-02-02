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
	private Boolean areLinkRecommendationsIncludedInCalculation;

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
		areLinkRecommendationsIncludedInCalculation = false;
	}

	public ChangeImpactAnalysisConfiguration(float decayValue, float threshold, long context,
			List<ChangePropagationRule> propagationRules) {
		this.decayValue = decayValue;
		this.threshold = threshold;
		this.linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
		this.context = context;
		this.propagationRules = propagationRules;
		this.areLinkRecommendationsIncludedInCalculation = false;
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

	/**
	 * @issue How can we make sure that changing the number of link recommendation
	 *        rules results in a changed number in the frontend?
	 * @decision We return the default rules if the number stored in the settings is
	 *           different to them!
	 * 
	 * @return CIA rules as {@link ChangePropagationRule} objects.
	 */
	@XmlElement
	public List<ChangePropagationRule> getPropagationRules() {
		if (propagationRules.size() != ChangePropagationRule.getDefaultRules().size()) {
			return ChangePropagationRule.getDefaultRules();
		}
		return propagationRules;
	}

	/**
	 * @param propagationRules
	 *            CIA rules as {@link ChangePropagationRule} objects.
	 */
	@JsonProperty
	public void setPropagationRules(List<ChangePropagationRule> propagationRules) {
		this.propagationRules = propagationRules;
	}

	@XmlElement
	public Boolean getAreLinkRecommendationsIncludedInCalculation() {
		return areLinkRecommendationsIncludedInCalculation;
	}

	public void setAreLinkRecommendationsIncludedInCalculation(Boolean areLinkRecommendationsIncludedInCalculation) {
		this.areLinkRecommendationsIncludedInCalculation = areLinkRecommendationsIncludedInCalculation;
	}

}