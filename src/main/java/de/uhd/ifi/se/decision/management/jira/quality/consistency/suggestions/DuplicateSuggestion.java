package de.uhd.ifi.se.decision.management.jira.quality.consistency.suggestions;

import org.codehaus.jackson.annotate.JsonProperty;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class DuplicateSuggestion implements Suggestion<KnowledgeElement> {
	@JsonProperty
	private final KnowledgeElement baseElement;
	@JsonProperty
	private final KnowledgeElement targetElement;
	@JsonProperty
	private final int startDuplicate; // of Issue i2
	@JsonProperty
	private final int length; // of Issue i2
	@JsonProperty
	private final String preprocessedSummary; // of Issue i2
	@JsonProperty
	private final String field;

	public DuplicateSuggestion(KnowledgeElement baseElement, KnowledgeElement targetElement, String preprocessedSummary,
			int startDuplicate, int length, String field) {
		this.baseElement = baseElement;
		this.targetElement = targetElement;
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

	public KnowledgeElement getBaseElement() {
		return baseElement;
	}

	@Override
	public KnowledgeElement getSuggestion() {
		return targetElement;
	}

	@Override
	public SuggestionType getSuggestionType() {
		return SuggestionType.DUPLICATE;
	}
}
