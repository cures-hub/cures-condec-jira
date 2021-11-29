package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestKnowledgeElementWithImpact extends TestSetUp {
    
    private KnowledgeElement element;
    private KnowledgeElementWithImpact elementWithImpactParent;
    private KnowledgeElementWithImpact elementWithImpactChild;
    private Map<String, Double> ruleMap = new HashMap<>();

    @Test
	public void testKnowledgeElementWithImpactEqualsWhenUsingSameKnowledgeElement() {
        element = new KnowledgeElement();
        elementWithImpactParent = new KnowledgeElementWithImpact(element);
        elementWithImpactChild = new KnowledgeElementWithImpact(
            element, 0.375, 0.75, 1.0, 1.0, ruleMap, "");    
		assertEquals(elementWithImpactParent, elementWithImpactChild);
	}
}
