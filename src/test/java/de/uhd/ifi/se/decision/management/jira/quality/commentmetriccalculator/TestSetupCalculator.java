package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import net.java.ao.EntityManager;

public class TestSetupCalculator extends TestSetUpWithIssues {
	private EntityManager entityManager;
	protected CommentMetricCalculator calculator;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoSysAdmin");
		addElementToDataBase(user);
		calculator = new CommentMetricCalculator((long) 1, user, "16");
	}

	@AfterClass
	public static void removeFolder() {
		File repo = new File(System.getProperty("user.home") + File.separator + "repository");
		if (repo.exists()) {
			repo.delete();
		}
	}

	protected void addElementToDataBase(ApplicationUser user) {
		PartOfJiraIssueText element;
		element = new PartOfJiraIssueTextImpl();
		element.setProject("TEST");
		element.setJiraIssueId(12);
		element.setId(1);
		element.setKey("TEST-12231");
		element.setType("Argument");
		element.setProject("TEST");
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(element, user);
	}
}
