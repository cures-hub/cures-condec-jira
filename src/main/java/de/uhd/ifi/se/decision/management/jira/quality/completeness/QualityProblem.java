package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import javax.xml.bind.annotation.XmlElement;

public class QualityProblem {

	private QualityProblemType type;
	private String explanation;

	public QualityProblem(QualityProblemType qualityProblemType) {
		this.type = qualityProblemType;
		this.setExplanation(qualityProblemType.getDescription());
	}

	@XmlElement
	public String getName() {
		return type.name();
	}

	@XmlElement
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public QualityProblemType getType() {
		return type;
	}
}