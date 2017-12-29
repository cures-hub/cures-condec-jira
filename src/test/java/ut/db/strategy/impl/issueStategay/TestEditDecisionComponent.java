package ut.db.strategy.impl.issueStategay;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

public class TestEditDecisionComponent extends TestIssueStartegySup {

	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresentationNullApplicUserNull() {
		issueStrat.editDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresentationFilledApplicUserNull() {
		DecisionRepresentation dec = new DecisionRepresentation();
		issueStrat.editDecisionComponent(dec, null);
	}
	
	@Test
	public void testDecisionRepresentationFilledApplicUserFilledRight() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrat.editDecisionComponent(dec, user));
	}
	
	@Test
	public void testDecisionRepresentationFilledApplicUserFilledWrong() {
		DecisionRepresentation dec = new DecisionRepresentation();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertNull(issueStrat.editDecisionComponent(dec, user));
	}
}
