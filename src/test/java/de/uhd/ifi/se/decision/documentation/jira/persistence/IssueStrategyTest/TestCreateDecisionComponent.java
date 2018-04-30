package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;

public class TestCreateDecisionComponent extends TestIssueStrategySetUp {
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresNullUserNull() {
		issueStrategy.insertDecisionKnowledgeElement(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testCreateDecisionComponentRepresFilledUserNull() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		issueStrategy.insertDecisionKnowledgeElement(dec, null);
	}
	
	@Test
	public void testCreateDecisionComponentRepresFilledUserFilledNoFails() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		dec.setProjectKey("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.insertDecisionKnowledgeElement(dec, user));
		
	}
	@Test
	public void testCreateDecisionComponentRepresFilledUserFilledWithFails() {
		DecisionKnowledgeElementImpl dec = new DecisionKnowledgeElementImpl();
		dec.setProjectKey("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertNull(issueStrategy.insertDecisionKnowledgeElement(dec, user));
		
	}
}
