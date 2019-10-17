package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetAdjacentElements extends TestJiraIssuePersistenceManagerSetUp {

	@Test
	public void testDecisionKnowledgeElementNull() {
		assertEquals(new ArrayList<DecisionKnowledgeElementImpl>(), issueStrategy.getAdjacentElements(null));
	}

	@Test
	public void testDecisionKnowledgeElementEmpty() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(0, issueStrategy.getAdjacentElements(element).size());
	}

	@Test
	public void testDecisionKnowledgeElementHasAllTypesOfChildren() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		Project project = JiraProjects.getTestProject();

		long i = 2;
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(5000, "TESTSummary", "TestDescription",
				KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000, DocumentationLocation.JIRAISSUE);
		element.setId(5000);

		issueStrategy.insertDecisionKnowledgeElement(element, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			if (type != KnowledgeType.DECISION) {
				DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
						"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i,
						DocumentationLocation.JIRAISSUE);
				issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
				Link link = new LinkImpl(decisionKnowledgeElement, element);
				link.setType("support");
				AbstractPersistenceManager.insertLink(link, user);
			}
			i++;
		}
		assertNotNull(issueStrategy.getAdjacentElements(element));
	}
}
