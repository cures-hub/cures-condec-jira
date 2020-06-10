package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

public interface DiscardSuggestionTester {

	void testInsertAndGetDiscardedSuggestion();

	void testInsertNullAsDiscardedSuggestion();

	void testReset();
}
