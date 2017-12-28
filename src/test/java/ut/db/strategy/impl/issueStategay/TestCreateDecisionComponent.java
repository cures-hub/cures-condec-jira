package ut.db.strategy.impl.issueStategay;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.MockUser;

public class TestCreateDecisionComponent extends TestIssueStartegySup {
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresNullUserNull() {
		issueStrat.createDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresFilledUserNull() {
		DecisionRepresentation dec = new DecisionRepresentation();
		issueStrat.createDecisionComponent(dec, null);
	}
	
	@Test
	public void testCreateDecisionComponentRepresFilledUserFilled() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("Test");
		issueStrat.createDecisionComponent(dec, user);
		
	}
}
