package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestComponentContextInformationProvider extends TestSetUp {

	private ComponentContextInformationProvider componentContextInformationProvider;
	private KnowledgeElement rootElement;
	private KnowledgeElement currentElement;

	@Before
	public void setUp() {
		init();
		componentContextInformationProvider = new ComponentContextInformationProvider();
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(2);
	}

	@Test
	public void testRootNoComponents() {
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		RecommendationScore score = componentContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(1.0, score.getValue(), 0.00);
		assertNotNull(score.getExplanation());
	}

	@Test
	public void testPEqualComponents() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		RecommendationScore score = componentContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(1.0, score.getValue(), 0.00);
	}

	@Test
	public void testRootOnlyComponent() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(4);
		RecommendationScore score = componentContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(0.0, score.getValue(), 0.00);
	}

	@Test
	public void testNoMatchingComponents() {
		currentElement = KnowledgeElements.getTestKnowledgeElements().get(3);
		RecommendationScore score = componentContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(0.0, score.getValue(), 0.00);
	}

	@Test
	public void testJiraIssueNull() {
		RecommendationScore score = componentContextInformationProvider.assessRelation(rootElement,
				new KnowledgeElement());
		assertEquals(1, score.getValue(), 0);

		score = componentContextInformationProvider.assessRelation(new KnowledgeElement(), new KnowledgeElement());
		assertEquals(1, score.getValue(), 0);
	}

	@Test
	public void testExplanation() {
		assertNotNull(componentContextInformationProvider.getExplanation());
	}

	@Test
	public void testDescription() {
		assertNotNull(componentContextInformationProvider.getDescription());
	}
}
