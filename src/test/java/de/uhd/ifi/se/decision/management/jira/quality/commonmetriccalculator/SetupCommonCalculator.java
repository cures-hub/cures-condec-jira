package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import org.junit.Before;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class SetupCommonCalculator extends TestSetUp {
	protected CommonMetricCalculator calculator;
	private long id = 1;
	private long jiraIssueId = 12;
	private long elemIssueId = 1;
	private String projectKey = "TEST";

	protected String baseIssueKey = "TEST-100";
	protected ApplicationUser user;
	protected DecisionKnowledgeElement decisionElement;
	protected DecisionKnowledgeElement argumentElement;
	protected DecisionKnowledgeElement issueElement;

	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		issueElement = addElementToDataBase(user, "Issue");
		decisionElement = addElementToDataBase(user, "Decision");
		argumentElement = addElementToDataBase(user, "Argument");

		calculator = new CommonMetricCalculator(1, user, "16");
	}

	protected PartOfJiraIssueText addElementToDataBase(ApplicationUser user, String type) {
		id++;
		PartOfJiraIssueText element;
		element = new PartOfJiraIssueTextImpl();
		element.setProject(projectKey);
		element.setJiraIssueId(jiraIssueId);
		element.setId(id);
		element.setKey(baseIssueKey + elemIssueId);
		element.setType(type);
		element.setProject("TEST");
		element.setDescription("Old");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager()
				.insertDecisionKnowledgeElement(element, user);
		return element;
	}
}
