package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestDecisionGroupContextInformationProvider extends TestSetUp{
    
    private DecisionGroupContextInformationProvider decisionGroupContextInformationProvider;
	private KnowledgeElement currentElement;
	private KnowledgeElement rootElement;

    @Before
	public void setUp() {
		init();
		decisionGroupContextInformationProvider = new DecisionGroupContextInformationProvider();
        currentElement = KnowledgeElements.getTestKnowledgeElements().get(10);
		rootElement = KnowledgeElements.getTestKnowledgeElements().get(9);
	}

    @Test
	public void testPropagationRootNoDecisionGroups() {
        RecommendationScore score = decisionGroupContextInformationProvider.assessRelation(rootElement, currentElement);
		
        assertEquals(1.0, score.getValue(), 0.00);
        assertEquals("DecisionGroupContextInformationProvider", score.getExplanation());
	}

	@Test
	public void testPropagationEqualDecisionGroups() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", rootElement);
		DecisionGroupPersistenceManager.insertGroup("TestGroup", currentElement);
        RecommendationScore score = decisionGroupContextInformationProvider.assessRelation(rootElement, currentElement);

		assertEquals(1.0, score.getValue(), 0.00);
	}
}
