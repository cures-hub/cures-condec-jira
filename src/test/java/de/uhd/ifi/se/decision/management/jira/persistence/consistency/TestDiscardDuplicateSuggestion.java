package de.uhd.ifi.se.decision.management.jira.persistence.consistency;

import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.addDiscardedDuplicate;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.getDiscardedDuplicates;
import static de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper.resetDiscardedSuggestions;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestDiscardDuplicateSuggestion extends TestSetUp implements DiscardSuggestionTester {
	private List<Issue> issues;

	@Before
	public void setUp() {
		TestSetUp.init();
		issues = JiraIssues.getTestJiraIssues();
	}

	@Override
	@Test
	public void testInsertAndGetDiscardedSuggestion() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		List<KnowledgeElement> discardedDuplicateSuggestions = getDiscardedDuplicates(knowledgeElement0);

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedDuplicateSuggestions.size());

		long id = addDiscardedDuplicate(knowledgeElement0, knowledgeElement1);
		discardedDuplicateSuggestions = getDiscardedDuplicates(knowledgeElement0);
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedDuplicateSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.", knowledgeElement1,
				discardedDuplicateSuggestions.get(0));

		long sameId = addDiscardedDuplicate(knowledgeElement0, knowledgeElement1);
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);

		long exceptionId = addDiscardedDuplicate(null, knowledgeElement1);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedDuplicate(knowledgeElement0, null);
		assertEquals("Id should be -1.", -1, exceptionId);

		exceptionId = addDiscardedDuplicate(null, null);
		assertEquals("Id should be -1.", -1, exceptionId);

	}

	@Override
	@Test
	public void testInsertNullAsDiscardedSuggestion() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		List<KnowledgeElement> discardedDuplicateSuggestions = getDiscardedDuplicates(knowledgeElement0);
		int discardedSuggestionsBeforeNullInsertion = discardedDuplicateSuggestions.size();
		addDiscardedDuplicate(knowledgeElement0, null);
		discardedDuplicateSuggestions = getDiscardedDuplicates(knowledgeElement0);
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
				discardedSuggestionsBeforeNullInsertion, discardedDuplicateSuggestions.size());

	}

	@Override
	@Test
	public void testReset() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		addDiscardedDuplicate(knowledgeElement0, knowledgeElement1);
		resetDiscardedSuggestions();
		List<KnowledgeElement> discardedDuplicateSuggestions = getDiscardedDuplicates(knowledgeElement0);
		assertEquals("No more suggestion should be discarded after reset.", 0, discardedDuplicateSuggestions.size());
	}

	@AfterEach
	public void cleanDatabase() {
		resetDiscardedSuggestions();
	}
}
