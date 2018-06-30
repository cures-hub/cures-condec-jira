package de.uhd.ifi.se.decision.documentation.jira.persistence.issuestrategy;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetElementsLinkedWithInwardLinks extends TestIssueStrategySetUp {

    @Test
    public void testElementNull() {
        assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(null).size());
    }

    @Test
    public void testElementNotExistend() {
        DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
        assertEquals(0, issueStrategy.getElementsLinkedWithInwardLinks(element).size());
    }
}
