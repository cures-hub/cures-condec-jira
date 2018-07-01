package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;

public class TestGetLinkedElements extends TestIssueStrategySetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getLinkedElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(),
				issueStrategy.getLinkedElements(decisionKnowledgeElement));
	}

	@Test
	public void testDecisionKnowledgeElementHasAllTypesOfChildren() {
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

		long i = 2;
		DecisionKnowledgeElementImpl decision = null;
		decision = new DecisionKnowledgeElementImpl((long) 5000, "TESTSummary", "TestDescription",
				KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000);
		decision.setId((long) 5000);

		issueStrategy.insertDecisionKnowledgeElement(decision, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			LinkImpl link = new LinkImpl();
			link.setLinkType("support");
			if (type != KnowledgeType.DECISION) {
				DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
						"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
				issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
				link.setOutgoingId(decision.getId());
				link.setIngoingId(decisionKnowledgeElement.getId());
				issueStrategy.insertLink(link, user);
			}
			i++;
		}
		assertNotNull(issueStrategy.getLinkedElements(decision));
	}
}
