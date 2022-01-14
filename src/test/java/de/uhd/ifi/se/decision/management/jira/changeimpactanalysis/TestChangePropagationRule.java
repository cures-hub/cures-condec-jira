package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestChangePropagationRule extends TestSetUp {
    
    @Test
    public void testGetPropagationRuleInputNull() {
        String propagationRuleName = null;
        assertTrue(ChangePropagationRule.getPropagationRule(propagationRuleName) == null);
    }

    @Test
    public void testGetPropagationRuleInputEmpty() {
        String propagationRuleName = "";
        assertTrue(ChangePropagationRule.getPropagationRule(propagationRuleName) == null);
    }

    @Test
    public void testGetPropagationRuleNoRuleMatch() {
        String propagationRuleName = "FooBar";
        assertTrue(ChangePropagationRule.getPropagationRule(propagationRuleName) == null);
    }

    @Test
    public void testGetPropagationRule() {
        String propagationRuleName = "Stop at elements with the same type as the selected element";
        assertEquals(ChangePropagationRule.STOP_AT_SAME_ELEMENT_TYPE, ChangePropagationRule.getPropagationRule(propagationRuleName));
    }
}
