package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.Test;

import java.util.List;

import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.addDiscardedDuplicate;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.getDiscardedDuplicates;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.resetDiscardedDuplicates;
import static org.junit.Assert.assertEquals;

public class TestDiscardDuplicateSuggestion extends TestSetUp implements DiscardSuggestionTester {
	private List<MutableIssue> issues;
	private String projectKey;

	@Before
	public void setUp() {
		TestSetUp.init();
		issues = JiraIssues.getTestJiraIssues();
		projectKey = issues.get(0).getProjectObject().getKey();
	}

	@Override
	@Test
	public void testInsertAndGetDiscardedSuggestion() {
		List<Issue> discardedDuplicateSuggestions = getDiscardedDuplicates(issues.get(0).getKey());

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedDuplicateSuggestions.size());

		long id = addDiscardedDuplicate(issues.get(0).getKey(), issues.get(1).getKey(), projectKey);
		discardedDuplicateSuggestions = getDiscardedDuplicates(issues.get(0).getKey());
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedDuplicateSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.", issues.get(1).getKey(), discardedDuplicateSuggestions.get(0).getKey());

		long sameId = addDiscardedDuplicate(issues.get(0).getKey(), issues.get(1).getKey(), projectKey);
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);

		long exceptionId = addDiscardedDuplicate(null, issues.get(1).getKey(), projectKey);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedDuplicate(issues.get(0).getKey(), null, projectKey);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedDuplicate(issues.get(0).getKey(), issues.get(1).getKey(), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedDuplicate(null, null, null);
		assertEquals("Id should be -1.", -1, exceptionId);

	}

	@Override
	@Test
	public void testInsertNullAsDiscardedSuggestion() {
		List<Issue> discardedDuplicateSuggestions = getDiscardedDuplicates(issues.get(0).getKey());
		int discardedSuggestionsBeforeNullInsertion = discardedDuplicateSuggestions.size();
		addDiscardedDuplicate(issues.get(0).getKey(), null, projectKey);
		discardedDuplicateSuggestions = getDiscardedDuplicates(issues.get(0).getKey());
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
			discardedSuggestionsBeforeNullInsertion, discardedDuplicateSuggestions.size());

	}

	@Override
	@Test
	public void testReset() {
		addDiscardedDuplicate(issues.get(0).getKey(), issues.get(1).getKey(), projectKey);
		resetDiscardedDuplicates();
		List<Issue> discardedDuplicateSuggestions = getDiscardedDuplicates(issues.get(0).getKey());
		assertEquals("No more suggestion should be discarded after reset.", 0, discardedDuplicateSuggestions.size());
	}

	@AfterEach
	public void cleanDatabase(){
		resetDiscardedDuplicates();
	}
}
