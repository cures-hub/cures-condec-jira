package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.KnowledgeType;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;

public class TestEditDecisionComponent extends TestIssueStrategySetUp {

	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresentationNullApplicUserNull() {
		issueStrategy.updateDecisionKnowledgeElement(null, null);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testDecisionRepresentationFilledApplicUserNull() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		issueStrategy.updateDecisionKnowledgeElement(dec, null);
	}
	
	@Test
	public void testDecisionRepresentationFilledApplicUserFilledRight() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("NoFails");
		assertNotNull(issueStrategy.updateDecisionKnowledgeElement(dec, user));
	}
	
	@Test
	public void testDecisionRepresentationFilledApplicUserFilledWrong() {
		DecisionKnowledgeElement dec = new DecisionKnowledgeElement();
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType(KnowledgeType.SOLUTION);
		ApplicationUser user = new MockApplicationUser("WithFails");
		assertFalse(issueStrategy.updateDecisionKnowledgeElement(dec, user));
	}
}
