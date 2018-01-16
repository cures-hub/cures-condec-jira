package ut.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

public class TestDeleteDecisionComponent extends TestIssueStrategySetUp {
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresNullUserNull() {
		issueStrategy.deleteDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresFilledUserNull() {
		DecisionRepresentation dec = new DecisionRepresentation();
		issueStrategy.deleteDecisionComponent(dec, null);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledRight() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertTrue(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresIssueUnvalid() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledResultErrors() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithResFails");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresNoResultErrors() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("ValidNoResErrors");
		assertFalse(issueStrategy.deleteDecisionComponent(dec, user));
	}

}
