package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUser;

public class TestGetAdjacentElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getAdjacentElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getAdjacentElements(element));
	}

	@Test
	public void testDecisionKnowledgeElementHasAllTypesOfChildren() {
		ApplicationUser user = JiraUser.SYS_ADMIN.getApplicationUser();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

		long i = 2;
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(5000, "TESTSummary", "TestDescription",
				KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000, DocumentationLocation.JIRAISSUE);
		element.setId(5000);

		issueStrategy.insertDecisionKnowledgeElement(element, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			Link link = new LinkImpl();
			link.setType("support");
			if (type != KnowledgeType.DECISION) {
				DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
						"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i,
						DocumentationLocation.JIRAISSUE);
				issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
				link.setDestinationElement(element);
				link.setSourceElement(decisionKnowledgeElement);
				AbstractPersistenceManager.insertLink(link, user);
			}
			i++;
		}
		assertNotNull(issueStrategy.getAdjacentElements(element));
	}
}
