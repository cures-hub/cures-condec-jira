package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.SimilarityScore;

public class LinkSuggestion implements Comparable<LinkSuggestion> {

	private Issue baseIssue;
	private Issue targetIssue;
	private SimilarityScore score;

	public LinkSuggestion(Issue baseIssue, Issue targetIssue) {
		this.baseIssue = baseIssue;
		this.targetIssue = targetIssue;
		this.score = new SimilarityScore();
	}


	public Issue getBaseIssue() {
		return baseIssue;
	}

	public Issue getTargetIssue() {
		return targetIssue;
	}

	public Double getTotalScore() {
		return score.getTotal();
	}

	public SimilarityScore getScore() {
		return score;
	}

	public void addToScore(Double score, String field) {
		this.score.put(field, score);
	}


	@Override
	public int compareTo(LinkSuggestion o) {
		if (o == null){
			return -1;
		}
		int compareValue = 0;
		if (this.getTotalScore() > o.getTotalScore()) {
			compareValue = 1;
		} else {
			compareValue = -1;
		}
		return compareValue;
	}
}
