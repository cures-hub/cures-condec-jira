package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import javax.xml.bind.annotation.XmlElement;

public class QualityProblem {

	private String name;
	private String explanation;

	public QualityProblem(QualityProblemType qualityProblemType) {
		this.name = qualityProblemType.name();
		this.setExplanation(qualityProblemType.getDescription());
	}

	@XmlElement
	public String getName() {
		return name;
	}

	@XmlElement
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

}
