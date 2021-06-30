package de.uhd.ifi.se.decision.management.jira.view.matrix;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;

/**
 * Models a {@link KnowledgeElement} atogether with its quality and change
 * impact highlighting in the {@link KnowledgeGraph}.
 * 
 * @see FilterSettings#areQualityProblemHighlighted()
 * @see FilterSettings#areChangeImpactsHighlighted()
 */
public class ElementWithHighlighting {

	private KnowledgeElement element;
	private String qualityColor;
	private String qualityProblemExplanation;
	private String changeImpactColor;

	public ElementWithHighlighting(KnowledgeElement element) {
		this.element = element;
		this.qualityColor = "#000000";
		this.qualityProblemExplanation = "";
		this.changeImpactColor = "#FFFFFF";
	}

	@XmlElement
	public KnowledgeElement getElement() {
		return element;
	}

	@XmlElement
	public String getQualityColor() {
		return qualityColor;
	}

	public void setQualityColor(String qualityColor) {
		this.qualityColor = qualityColor;
	}

	@XmlElement
	public String getQualityProblemExplanation() {
		return qualityProblemExplanation;
	}

	public void setQualityProblemExplanation(String qualityProblemExplanation) {
		this.qualityProblemExplanation = qualityProblemExplanation;
	}

	@XmlElement
	public String getChangeImpactColor() {
		return changeImpactColor;
	}

	public void setChangeImpactColor(String changeImpactColor) {
		this.changeImpactColor = changeImpactColor;
	}
}