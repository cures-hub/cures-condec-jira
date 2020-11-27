package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.addDiscardedLinkSuggestions;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.resetDiscardedSuggestions;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestDiscardLinkSuggestions extends TestSetUp implements DiscardSuggestionTester {

	private List<Issue> issues;

	@Before
	public void setUp() {
		TestSetUp.init();
		issues = JiraIssues.getTestJiraIssues();
	}

	@Override
	@Test
	public void testInsertAndGetDiscardedSuggestion() {
		List<KnowledgeElement> discardedSuggestions = ConsistencyPersistenceHelper
				.getDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)));

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedSuggestions.size());

		long id = addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)), new KnowledgeElement(issues.get(1)));

		discardedSuggestions = ConsistencyPersistenceHelper
				.getDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)));
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.",
				new KnowledgeElement(issues.get(1)).getId(), discardedSuggestions.get(0).getId());

		long sameId = addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)),
				new KnowledgeElement(issues.get(1)));
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);

		long exceptionId = addDiscardedLinkSuggestions(null, new KnowledgeElement(issues.get(1)));
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(null, new KnowledgeElement(issues.get(1)));
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)), null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedLinkSuggestions(null, null);
		assertEquals("Id should be -1.", -1, exceptionId);
	}

	@Override
	@Test
	public void testInsertNullAsDiscardedSuggestion() {
		List<KnowledgeElement> discardedSuggestions = ConsistencyPersistenceHelper
				.getDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)));
		int discardedSuggestionsBeforeNullInsertion = discardedSuggestions.size();
		addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)), null);
		discardedSuggestions = ConsistencyPersistenceHelper
				.getDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)));
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
				discardedSuggestionsBeforeNullInsertion, discardedSuggestions.size());

	}

	@Override
	@Test
	public void testReset() {
		addDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)), new KnowledgeElement(issues.get(1)));
		resetDiscardedSuggestions();

		List<KnowledgeElement> discardedSuggestions = ConsistencyPersistenceHelper
				.getDiscardedLinkSuggestions(new KnowledgeElement(issues.get(0)));
		assertEquals("No more suggestion should be discarded after reset.", 0, discardedSuggestions.size());

	}

	@AfterEach
	public void cleanDatabase() {
		resetDiscardedSuggestions();
	}
}
