package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

public interface DiscardSuggestionTester {

	void testInsertAndGetDiscardedSuggestion();

	void testInsertNullAsDiscardedSuggestion();

	void testReset();
}
