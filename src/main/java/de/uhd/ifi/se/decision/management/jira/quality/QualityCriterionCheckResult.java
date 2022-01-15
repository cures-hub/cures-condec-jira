package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class QualityCriterionCheckResult {

	private QualityCriterionType type;
	private String explanation;
	private boolean isCriterionViolated;

	public QualityCriterionCheckResult(QualityCriterionType qualityProblemType) {
		this(qualityProblemType, true);
	}

	public QualityCriterionCheckResult(QualityCriterionType qualityProblemType, boolean isCriterionViolated) {
		this.type = qualityProblemType;
		setCriterionViolated(isCriterionViolated);
	}

	@XmlElement
	public String getName() {
		return type.toString();
	}

	@XmlElement
	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public void appendExplanation(String explanation) {
		this.explanation += " " + explanation;
	}

	public QualityCriterionType getType() {
		return type;
	}

	@XmlElement
	public boolean isCriterionViolated() {
		return isCriterionViolated;
	}

	public void setCriterionViolated(boolean isCriterionViolated) {
		this.isCriterionViolated = isCriterionViolated;
		if (explanation == null) {
			if (isCriterionViolated) {
				this.setExplanation(type.getViolationDescription());
			} else {
				this.setExplanation(type.getFulfillmentDescription());
			}
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof QualityCriterionCheckResult)) {
			return false;
		}
		QualityCriterionCheckResult otherProblem = (QualityCriterionCheckResult) object;
		return type == otherProblem.getType();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getExplanation());
	}
}