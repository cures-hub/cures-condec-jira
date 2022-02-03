package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestKnowledgeElementWithImpact extends TestSetUp {
    
    private KnowledgeElement element;
    private KnowledgeElementWithImpact elementWithImpact;

	@Before
	public void setUp() {
		init();
        element = new KnowledgeElement();
        elementWithImpact = new KnowledgeElementWithImpact(element);
	}
    
    @Test
	public void testEqualsUsingSameKnowledgeElement() {
        Map<String, Double> ruleMap = new HashMap<>();

        KnowledgeElementWithImpact elementWithImpactChild = new KnowledgeElementWithImpact(
            element, 0.375, 0.75, 1.0, 1.0, ruleMap, "");   
		assertEquals(elementWithImpact, elementWithImpactChild);
        assertEquals(element, elementWithImpactChild);
        assertEquals(element, elementWithImpact);
	}

    @Test
    public void testEqualsUsingSameObjects() {
        assertTrue(elementWithImpact.equals(elementWithImpact));
    }

    @Test
    public void testEqualsDifferentIds() {
        KnowledgeElement element = KnowledgeElements.getDecision();
        element.setId((long) 1);
        KnowledgeElementWithImpact elementWithImpact = new KnowledgeElementWithImpact(element);
        element = KnowledgeElements.getAlternative();
        element.setId((long) 1);
        KnowledgeElementWithImpact anotherElementWithImpact = new KnowledgeElementWithImpact(element);

        assertTrue(elementWithImpact.equals(anotherElementWithImpact));
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsUsingDifferentObjects() {
        String string = "This is a test string!";
        assertFalse(elementWithImpact.equals(string));
    }

    @Test
    public void testSettersAndGetters() {
        elementWithImpact.setImpactValue(0.72);
        assertEquals(0.72, elementWithImpact.getImpactValue(), 0.05);

        elementWithImpact.setRuleBasedValue(0.72);
        assertEquals(0.72, elementWithImpact.getRuleBasedValue(), 0.05);
        
        elementWithImpact.setLinkTypeWeight(0.72);
        assertEquals(0.72, elementWithImpact.getLinkTypeWeight(), 0.05);

        elementWithImpact.setParentImpact(0.72);
        assertEquals(0.72, elementWithImpact.getParentImpact(), 0.05);

        Map<String, Double> propagationRules = new HashMap<>();
        propagationRules.put("BarFoo", 0.72);
        elementWithImpact.setPropagationRules(propagationRules);
        assertEquals(1, elementWithImpact.getPropagationRules().size());

        elementWithImpact.setImpactExplanation("Foobar");
        assertEquals("Foobar", elementWithImpact.getImpactExplanation());
    }
}
