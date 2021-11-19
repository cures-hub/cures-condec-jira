package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Objects;

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

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof QualityProblem)) {
			return false;
		}
		QualityProblem otherProblem = (QualityProblem) object;
		return type == otherProblem.getType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getExplanation());
	}
}