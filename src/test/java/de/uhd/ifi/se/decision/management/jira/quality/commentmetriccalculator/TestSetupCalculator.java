package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;

public abstract class TestSetupCalculator extends TestSetUpWithIssues {
	protected CommentMetricCalculator calculator;

	@Before
	public void setUp() {
		initialization();
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
