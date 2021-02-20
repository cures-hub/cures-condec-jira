package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

public class CiaSettings {

	private Float decayValue;

	private Float threshold;

	private Map<String, Float> linkImpact;

	@JsonCreator
	public CiaSettings() {
		decayValue = 0.75f;
		threshold = 0.25f;
		linkImpact = new HashMap<>();
		DecisionKnowledgeProject.getAllNamesOfLinkTypes().forEach(entry -> {
			linkImpact.put(entry, 1.0f);
		});
	}

	@XmlElement(name = "decayValue")
	public Float getDecayValue() {
		return decayValue;
	}

	@JsonProperty("decayValue")
	public void setDecayValue(Float decayValue) {
		this.decayValue = decayValue;
	}

	@XmlElement(name = "threshold")
	public Float getThreshold() {
		return threshold;
	}

	@JsonProperty("threshold")
	public void setThreshold(Float threshold) {
		this.threshold = threshold;
	}

	@XmlElement(name = "linkImpact")
	public Map<String, Float> getLinkImpact() {
		return linkImpact;
	}

	@JsonProperty("linkImpact")
	public void setLinkImpact(Map<String, Float> linkImpact) {
		this.linkImpact = linkImpact;
	}
}
