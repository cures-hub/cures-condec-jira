package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.issue.Issue;

public class LinkSuggestion {

	public LinkSuggestion(Issue baseIssue, Issue targetIssue, Double score) {
		this.baseIssue = baseIssue;
		this.targetIssue = targetIssue;
		this.score = score;
	}

	private Issue baseIssue;
	private Issue targetIssue;
	private Double score;


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


}
