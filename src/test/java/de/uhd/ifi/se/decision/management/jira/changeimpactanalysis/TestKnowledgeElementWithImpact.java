package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestKnowledgeElementWithImpact extends TestSetUp {
    
    @Test
	public void testKnowledgeElementWithImpactEqualsWhenUsingSameKnowledgeElement() {
        KnowledgeElement element = new KnowledgeElement();
        KnowledgeElementWithImpact elementWithImpact = new KnowledgeElementWithImpact(element);
        Map<String, Double> ruleMap = new HashMap<>();

        KnowledgeElementWithImpact elementWithImpactChild = new KnowledgeElementWithImpact(
            element, 0.375, 0.75, 1.0, 1.0, ruleMap, "");   
		assertEquals(elementWithImpact, elementWithImpactChild);
        assertEquals(element, elementWithImpactChild);
        assertEquals(element, elementWithImpact);
	}
}
