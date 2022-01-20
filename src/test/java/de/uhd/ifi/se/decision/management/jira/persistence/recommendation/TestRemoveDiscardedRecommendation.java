package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRemoveDiscardedRecommendation extends TestSetUp {

	private LinkRecommendation recommendation;

	@Before
	public void setUp() {
		TestSetUp.init();
		KnowledgeElement knowledgeElement0 = KnowledgeElements.getTestKnowledgeElement();
		KnowledgeElement knowledgeElement1 = KnowledgeElements.getSolvedDecisionProblem();
		recommendation = new LinkRecommendation(knowledgeElement0, knowledgeElement1);
		DiscardedRecommendationPersistenceManager.saveDiscardedRecommendation(recommendation);
	}

	@Test
	@NonTransactional
	public void testRemovingSuccessful() {
		assertTrue(DiscardedRecommendationPersistenceManager.removeDiscardedRecommendation(recommendation));
	}

	@Test
	@NonTransactional
	public void testRemovingNotSuccessfulBecauseNotDiscarded() {
		LinkRecommendation otherRecommendation = new LinkRecommendation(KnowledgeElements.getDecision(),
				KnowledgeElements.getAlternative());
		assertFalse(DiscardedRecommendationPersistenceManager.removeDiscardedRecommendation(otherRecommendation));
	}

}
