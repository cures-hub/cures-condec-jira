package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestDecisionProblemContextInformationProvider extends TestSetUp {

	private DecisionProblemContextInformationProvider decisionProblemContextInformationProvider;
	private KnowledgeElement currentElement;
	private KnowledgeElement rootElement;

	@Before
	public void setUp() {
		init();
		decisionProblemContextInformationProvider = new DecisionProblemContextInformationProvider();
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(0);
	}

	@Test
	public void testPropagationNonDecisionProblem() {
		currentElement = KnowledgeElements.getOtherWorkItem();
		RecommendationScore score = decisionProblemContextInformationProvider.assessRelation(rootElement,
				currentElement);

		assertEquals(0.0, score.getValue(), 0.00);
		assertNotNull(score.getExplanation());
	}

	@Test
	public void testPropagationDecisionProblem() {
		currentElement = KnowledgeElements.getUnsolvedDecisionProblem();
		RecommendationScore score = decisionProblemContextInformationProvider.assessRelation(rootElement,
				currentElement);

		assertEquals(1.0, score.getValue(), 0.00);
	}

	@Test
	public void testExplanation() {
		assertNotNull(decisionProblemContextInformationProvider.getExplanation());
	}

	@Test
	public void testDescription() {
		assertNotNull(decisionProblemContextInformationProvider.getDescription());
	}
}
