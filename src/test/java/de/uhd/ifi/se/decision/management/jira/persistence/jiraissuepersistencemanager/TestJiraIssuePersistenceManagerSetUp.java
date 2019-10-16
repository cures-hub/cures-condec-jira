package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import org.junit.BeforeClass;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class TestJiraIssuePersistenceManagerSetUp extends TestSetUp {

	protected static JiraIssuePersistenceManager issueStrategy;
	protected static int numberOfElements;
	protected static ApplicationUser user;
	protected static Link link;

	@BeforeClass
	public static void setUp() {
		init();
		issueStrategy = new JiraIssuePersistenceManager("TEST");
		numberOfElements = issueStrategy.getDecisionKnowledgeElements().size();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		link = new LinkImpl(1,4,DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType(LinkType.RELATE.toString());
	}
}
