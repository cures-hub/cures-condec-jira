package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import net.java.ao.EntityManager;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.File;

public class TestSetupCalculator extends TestSetUpWithIssues {
	private EntityManager entityManager;
	protected CommonMetricCalculator calculator;
	private long id=1;
	private long jiraIssueId =12;
	private long elemIssueId =1;
	private String projectKey = "TEST";

	protected String baseIssueKey = "TEST-100";
	protected ApplicationUser user;
	protected DecisionKnowledgeElement decisionElement;
	protected DecisionKnowledgeElement argumentElement;
	protected DecisionKnowledgeElement issueElement;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		user = ComponentAccessor.getUserManager().getUserByName("NoSysAdmin");
		issueElement = addElementToDataBase(user,"Issue");
		decisionElement = addElementToDataBase(user,"Decision");
		argumentElement = addElementToDataBase(user,"Argument");

		calculator = new CommonMetricCalculator((long) 1, user, "16");
	}

	protected void linkElements(ApplicationUser user, DecisionKnowledgeElement sourceElement
			, DecisionKnowledgeElement destinationElement, String type) {
		Link link = new LinkImpl();
		link.setType(type);
		link.setSourceElement(sourceElement);
		link.setDestinationElement(destinationElement);
		JiraIssueTextPersistenceManager.insertLink(link,user);
	}

	@AfterClass
	public static void removeFolder() {
		File repo = new File(System.getProperty("user.home") + File.separator + "repository");
		if (repo.exists()) {
			repo.delete();
		}
	}

	protected PartOfJiraIssueText addElementToDataBase(ApplicationUser user, String type ) {
		id++;
		PartOfJiraIssueText element;
		element = new PartOfJiraIssueTextImpl();
		element.setProject(projectKey);
		element.setJiraIssueId(jiraIssueId);
		element.setId(id);
		element.setKey(baseIssueKey+ elemIssueId);
		element.setType(type);
		element.setProject("TEST");
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		JiraIssueTextPersistenceManager.insertDecisionKnowledgeElement(element, user);
		return element;
	}
}
