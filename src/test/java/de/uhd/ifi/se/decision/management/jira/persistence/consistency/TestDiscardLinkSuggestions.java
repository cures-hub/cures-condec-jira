package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.addDiscardedSuggestions;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.getDiscardedSuggestions;
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
		List<Issue> discardedSuggestions = getDiscardedSuggestions(issues.get(0));

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedSuggestions.size());

		long id = addDiscardedSuggestions(issues.get(0), issues.get(1));

		discardedSuggestions = getDiscardedSuggestions(issues.get(0));
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.", issues.get(1).getKey(), discardedSuggestions.get(0).getKey());

		long sameId = addDiscardedSuggestions(issues.get(0), issues.get(1));
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);


		long exceptionId = addDiscardedSuggestions(null, issues.get(1));
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedSuggestions(issues.get(0), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedSuggestions(null, issues.get(1).getKey(), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedSuggestions(issues.get(0).getKey(), null, null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedSuggestions(issues.get(0).getKey(), issues.get(1).getKey(), null);
		assertEquals("Id should be -1.", -1, exceptionId);


		exceptionId = addDiscardedSuggestions(null, null, null);
		assertEquals("Id should be -1.", -1, exceptionId);
	}

	@Test
	public void testInsertNullAsDiscardedSuggestion() {
		List<Issue> discardedSuggestions = getDiscardedSuggestions(issues.get(0));
		int discardedSuggestionsBeforeNullInsertion = discardedSuggestions.size();
		addDiscardedSuggestions(issues.get(0), null);
		discardedSuggestions = getDiscardedSuggestions(issues.get(0));
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
			discardedSuggestionsBeforeNullInsertion, discardedSuggestions.size());

	}

	@Override
	@Test
	public void testReset() {
		addDiscardedSuggestions(issues.get(0), issues.get(1));
		resetDiscardedSuggestions();

	List<Issue> discardedSuggestions = getDiscardedSuggestions(issues.get(0));
		assertEquals("No more suggestion should be discarded after reset.", 0, discardedSuggestions.size());

	}

	@AfterEach
	public void cleanDatabase() {
		resetDiscardedSuggestions();
	}
}
