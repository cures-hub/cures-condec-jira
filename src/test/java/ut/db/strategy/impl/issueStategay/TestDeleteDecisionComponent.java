package ut.db.strategy.impl.issueStategay;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

public class TestDeleteDecisionComponent extends TestIssueStartegySup {
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresNullUserNull() {
		issueStrat.deleteDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresFilledUserNull() {
		DecisionRepresentation dec = new DecisionRepresentation();
		issueStrat.deleteDecisionComponent(dec, null);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledRight() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("NoFails");
		issueStrat.deleteDecisionComponent(dec, user);
	}
	
	@Test
	public void testDecisionRepresFilledUserFilledWrong() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		issueStrat.deleteDecisionComponent(dec, user);
	}

}
