package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.issueStrategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Type;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;

/**
 * @author Tim Kuchenbuch
 */
public class TestDeleteDecisionComponent extends TestIssueStrategySetUp {
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresNullUserNull() {
		issueStrategy.deleteDecisionKnowledgeElement(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresFilledUserNull() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		issueStrategy.deleteDecisionKnowledgeElement(dec, null);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledRight() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(Type.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertTrue(issueStrategy.deleteDecisionKnowledgeElement(dec, user));
	}
	
	@Test
	public void testDecisionRepresIssueUnvalid() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(Type.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertFalse(issueStrategy.deleteDecisionKnowledgeElement(dec, user));
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledResultErrors() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(Type.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithResFails");
		assertFalse(issueStrategy.deleteDecisionKnowledgeElement(dec, user));
	}
	
	@Test
	public void testDecisionRepresNoResultErrors() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(Type.SOLUTION);
		ApplicationUser user = new MockApplicationUser("ValidNoResErrors");
		assertFalse(issueStrategy.deleteDecisionKnowledgeElement(dec, user));
	}

}
