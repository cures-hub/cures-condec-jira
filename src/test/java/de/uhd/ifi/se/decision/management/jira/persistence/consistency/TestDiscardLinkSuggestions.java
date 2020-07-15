package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.addDiscardedLinkSuggestions;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.resetDiscardedSuggestions;
import static org.junit.Assert.assertEquals;


public class TestDiscardLinkSuggestions extends TestSetUp implements DiscardSuggestionTester {

	private List<MutableIssue> issues;

	@Before
	public void setUp() {
		TestSetUp.init();
		issues = JiraIssues.getTestJiraIssues();
	}

	@Test
	public void testInsertAndGetDiscardedSuggestion() {
		List<Issue> discardedSuggestions = ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(issues.get(0));

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedSuggestions.size());

		long id = addDiscardedLinkSuggestions(issues.get(0), issues.get(1));

		discardedSuggestions = ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(issues.get(0));
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.", issues.get(1).getKey(), discardedSuggestions.get(0).getKey());

		long sameId = addDiscardedLinkSuggestions(issues.get(0), issues.get(1));
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);


		long exceptionId = addDiscardedLinkSuggestions(null, issues.get(1));
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(issues.get(0), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(null, issues.get(1).getKey(), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(issues.get(0).getKey(), null, null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(issues.get(0).getKey(), issues.get(1).getKey(), null);
		assertEquals("Id should be -1.", -1, exceptionId);


		exceptionId = addDiscardedLinkSuggestions(null, null, null);
		assertEquals("Id should be -1.", -1, exceptionId);
	}

	@Test
	public void testInsertNullAsDiscardedSuggestion() {
		List<Issue> discardedSuggestions = ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(issues.get(0));
		int discardedSuggestionsBeforeNullInsertion = discardedSuggestions.size();
		addDiscardedLinkSuggestions(issues.get(0), null);
		discardedSuggestions = ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(issues.get(0));
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
			discardedSuggestionsBeforeNullInsertion, discardedSuggestions.size());

	}

	@Override
	@Test
	public void testReset() {
		addDiscardedLinkSuggestions(issues.get(0), issues.get(1));
		resetDiscardedSuggestions();

	List<Issue> discardedSuggestions = ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(issues.get(0));
		assertEquals("No more suggestion should be discarded after reset.", 0, discardedSuggestions.size());

	}

	@AfterEach
	public void cleanDatabase() {
		resetDiscardedSuggestions();
	}
}
