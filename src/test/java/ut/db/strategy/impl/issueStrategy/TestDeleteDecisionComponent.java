package ut.db.strategy.impl.issueStrategy;

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
		issueStrategy.deleteDecisionComponent(dec, user);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledWrong() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		issueStrategy.deleteDecisionComponent(dec, user);
	}

}
