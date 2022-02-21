package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestKnowledgeTypeContextInformationProvider extends TestSetUp {

	private KnowledgeTypeContextInformationProvider knowledgeTypeContextInformationProvider;
	private KnowledgeElement currentElement;
	private KnowledgeElement rootElement;

	@Before
	public void setUp() {
		init();
		knowledgeTypeContextInformationProvider = new KnowledgeTypeContextInformationProvider();
	}

	@Test
	public void testNonEqualType() {
		currentElement = KnowledgeElements.getOtherWorkItem();
		rootElement = KnowledgeElements.getAlternative();
		RecommendationScore score = knowledgeTypeContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(0.0, score.getValue(), 0.00);
		assertNotNull(score.getExplanation());
	}

	@Test
	public void testEqualType() {
		currentElement = KnowledgeElements.getAlternative();
		rootElement = KnowledgeElements.getAlternative();
		RecommendationScore score = knowledgeTypeContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(1.0, score.getValue(), 0.00);
	}

	@Test
	public void testExplanation() {
		assertNotNull(knowledgeTypeContextInformationProvider.getExplanation());
	}

	@Test
	public void testDescription() {
		assertNotNull(knowledgeTypeContextInformationProvider.getDescription());
	}
}
