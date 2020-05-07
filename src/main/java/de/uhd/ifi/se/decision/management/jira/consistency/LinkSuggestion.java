package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;

public class LinkSuggestion implements Comparable<LinkSuggestion> {

	private Issue baseIssue;
	private Issue targetIssue;
	private Double score;

	public LinkSuggestion(Issue baseIssue, Issue targetIssue, Double score) {
		this.baseIssue = baseIssue;
		this.targetIssue = targetIssue;
		this.score = score;
	}


	public Issue getBaseIssue() {
		return baseIssue;
	}

	public void setBaseIssue(Issue baseIssue) {
		this.baseIssue = baseIssue;
	}

	public Issue getTargetIssue() {
		return targetIssue;
	}

	public void setTargetIssue(Issue targetIssue) {
		this.targetIssue = targetIssue;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public void addToScore(Double toAdd) {
		this.score += toAdd;
	}


	@Override
	public int compareTo(LinkSuggestion o) {
		if (o == null){
			return -1;
		}
		int compareValue = 0;
		if (this.getScore() > o.getScore()) {
			compareValue = 1;
		} else {
			compareValue = -1;
		}
		return compareValue;
	}
}
