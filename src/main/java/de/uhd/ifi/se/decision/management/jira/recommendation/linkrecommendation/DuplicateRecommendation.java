package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;

public class DuplicateRecommendation extends LinkRecommendation {

	private static final long serialVersionUID = 1L;
	@JsonProperty
	private final int startDuplicate; // of Issue i2
	@JsonProperty
	private final int length; // of Issue i2
	@JsonProperty
	private final String preprocessedSummary; // of Issue i2
	@JsonProperty
	private final String field;

	public DuplicateRecommendation(KnowledgeElement baseElement, KnowledgeElement targetElement, String preprocessedSummary,
			int startDuplicate, int length, String field) {
		super(baseElement, targetElement);
		this.startDuplicate = startDuplicate;
		this.length = length;
		this.field = field;
		this.preprocessedSummary = preprocessedSummary;
	}

	public int getStartDuplicate() {
		return startDuplicate;
	}

	public int getLength() {
		return length;
	}

	public String getField() {
		return field;
	}

	public String getPreprocessedSummary() {
		return preprocessedSummary;
	}

	@Override
	public RecommendationType getRecommendationType() {
		return RecommendationType.DUPLICATE;
	}

	@Override
	public RecommendationScore getScore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScore(RecommendationScore score) {
		// TODO Auto-generated method stub

	}
}
