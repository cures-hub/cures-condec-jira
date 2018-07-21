package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;

public class TestGetLinkedElements extends TestIssueStrategySetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getLinkedElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getLinkedElements(element));
	}

	@Test
	public void testDecisionKnowledgeElementHasAllTypesOfChildren() {
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

		long i = 2;
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(5000, "TESTSummary",
				"TestDescription", KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000);
		element.setId(5000);

		issueStrategy.insertDecisionKnowledgeElement(element, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			Link link = new LinkImpl();
			link.setLinkType("support");
			if (type != KnowledgeType.DECISION) {
				DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
						"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i);
				issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
				link.setDestinationElement(element.getId());
				link.setSourceElement(decisionKnowledgeElement.getId());
				issueStrategy.insertLink(link, user);
			}
			i++;
		}
		assertNotNull(issueStrategy.getLinkedElements(element));
	}
}
