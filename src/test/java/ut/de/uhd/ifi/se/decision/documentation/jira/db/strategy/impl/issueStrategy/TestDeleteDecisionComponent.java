package ut.de.uhd.ifi.se.decision.documentation.jira.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		issueStrategy.deleteDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresFilledUserNull() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		issueStrategy.deleteDecisionComponent(dec, null);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledRight() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertTrue(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresIssueUnvalid() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledResultErrors() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithResFails");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresNoResultErrors() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("ValidNoResErrors");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}

}
