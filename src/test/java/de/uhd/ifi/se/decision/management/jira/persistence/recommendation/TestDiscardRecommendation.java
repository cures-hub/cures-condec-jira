package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDiscardRecommendation extends TestSetUp {
	private List<Issue> issues;

	@Before
	public void setUp() {
		TestSetUp.init();
		issues = JiraIssues.getTestJiraIssues();
	}

	@Test
	@NonTransactional
	public void testInsertAndGetDiscardedSuggestion() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		List<KnowledgeElement> discardedDuplicateSuggestions = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(knowledgeElement0);

		assertEquals("Before insertion no discarded suggestion should exist.", 0, discardedDuplicateSuggestions.size());

		LinkRecommendation recommendation = new LinkRecommendation(knowledgeElement0, knowledgeElement1);
		long id = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		discardedDuplicateSuggestions = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(knowledgeElement0);
		assertEquals("After insertion one discarded suggestion should exist.", 1, discardedDuplicateSuggestions.size());

		assertEquals("The discarded suggestion should be the inserted issue.", knowledgeElement1,
				discardedDuplicateSuggestions.get(0));

		long sameId = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		assertEquals("Ids should be identical, because it represents the same link suggestion.", id, sameId);

		recommendation.setSourceElement(null);
		long exceptionId = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		assertEquals("Id should be -1.", -1, exceptionId);

		recommendation.setSourceElement(knowledgeElement0);
		recommendation.setDestinationElement(null);
		exceptionId = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		assertEquals("Id should be -1.", -1, exceptionId);

		recommendation.setSourceElement(null);
		exceptionId = DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		assertEquals("Id should be -1.", -1, exceptionId);

	}

	@Test
	@NonTransactional
	public void testInsertNullAsDiscardedSuggestion() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		List<KnowledgeElement> discardedDuplicateSuggestions = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(knowledgeElement0);
		int discardedSuggestionsBeforeNullInsertion = discardedDuplicateSuggestions.size();
		LinkRecommendation recommendation = new LinkRecommendation(knowledgeElement0, null);
		DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
		discardedDuplicateSuggestions = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(knowledgeElement0);
		assertEquals("After insertion of null as a discarded suggestion, no additional discarded issue should exist.",
				discardedSuggestionsBeforeNullInsertion, discardedDuplicateSuggestions.size());
	}
}
