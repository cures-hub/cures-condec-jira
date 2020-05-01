package de.uhd.ifi.se.decision.management.jira.classification;

public class AlreadyInTrainingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AlreadyInTrainingException(String s) {
		super(s);
	}
}
