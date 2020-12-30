package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuepersistencemanager;

import org.junit.Before;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class TestJiraIssuePersistenceManagerSetUp extends TestSetUp {

	protected static JiraIssuePersistenceManager issueStrategy;
	protected static int numberOfElements;
	protected static ApplicationUser user;
	protected static Link link;

	@Before
	public void setUp() {
		init();
		issueStrategy = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
		numberOfElements = issueStrategy.getKnowledgeElements().size();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = new Link(1, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType(LinkType.RELATE);
	}
}
