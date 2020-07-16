package de.uhd.ifi.se.decision.management.jira.consistency.suggestions;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.codehaus.jackson.annotate.JsonProperty;

public class DuplicateSuggestion implements Suggestion<KnowledgeElement> {
	@JsonProperty
	private final Issue i1;
	@JsonProperty
	private final Issue i2;
	@JsonProperty
	private final int startDuplicate;    //of Issue i2
	@JsonProperty
	private final int length;    //of Issue i2
	@JsonProperty
	private final String preprocessedSummary;    //of Issue i2
	@JsonProperty
	private final String field;

	public DuplicateSuggestion(Issue i1, Issue i2, String preprocessedSummary, int startDuplicate, int length, String field) {
		this.i1 = i1;
		this.i2 = i2;
		this.startDuplicate = startDuplicate;
		this.length = length;
		this.field = field;
		this.preprocessedSummary = preprocessedSummary;
	}

	public DuplicateSuggestion(KnowledgeElement i1, KnowledgeElement i2, String preprocessedSummary, int startDuplicate, int length, String field) {
		this(i1.getJiraIssue(), i2.getJiraIssue(), preprocessedSummary, startDuplicate, length, field);
	}


	public Issue getI1() {
		return i1;
	}

	public Issue getI2() {
		return i2;
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
	public KnowledgeElement getSuggestion() {
		return new KnowledgeElement(this.i2);
	}

	@Override
	public SuggestionType getType() {
		return SuggestionType.DUPLICATE;
	}
}
