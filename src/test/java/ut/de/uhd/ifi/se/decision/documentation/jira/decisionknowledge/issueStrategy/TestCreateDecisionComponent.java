package ut.de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.issueStrategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;

/**
 * @author Tim Kuchenbuch
 */
public class TestCreateDecisionComponent extends TestIssueStrategySetUp {
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresNullUserNull() {
		issueStrategy.createDecisionComponent(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresFilledUserNull() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		issueStrategy.createDecisionComponent(dec, null);
	}
	
	@Test
	public void testCreateDecisionComponentRepresFilledUserFilledNoFails() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.createDecisionComponent(dec, user));
		
	}
	@Test
	public void testCreateDecisionComponentRepresFilledUserFilledWithFails() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertNull(issueStrategy.createDecisionComponent(dec, user));
		
	}
}
