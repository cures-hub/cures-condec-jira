package de.uhd.ifi.se.decision.management.jira.consistency.suggestions;

import com.atlassian.jira.issue.Issue;
import org.codehaus.jackson.annotate.JsonProperty;

public class DuplicateFragment implements Suggestion<Issue> {
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

	public DuplicateFragment(Issue i1, Issue i2, String preprocessedSummary, int startDuplicate, int length, String field) {
		this.i1 = i1;
		this.i2 = i2;
		this.startDuplicate = startDuplicate;
		this.length = length;
		this.field = field;
		this.preprocessedSummary = preprocessedSummary;
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
	public Issue getSuggestion() {
		return this.i2;
	}
}
