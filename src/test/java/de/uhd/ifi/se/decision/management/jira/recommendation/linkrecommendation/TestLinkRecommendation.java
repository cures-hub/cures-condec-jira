package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestLinkRecommendation extends TestSetUp {

	private LinkRecommendation linkRecommendation;

	@Before
	public void setUp() {
		init();
		linkRecommendation = new LinkRecommendation(KnowledgeElements.getOtherWorkItem(),
				KnowledgeElements.getSolvedDecisionProblem());
	}

	@Test
	public void testRecommendationType() {
		assertEquals(RecommendationType.LINK, linkRecommendation.getRecommendationType());
	}
}
