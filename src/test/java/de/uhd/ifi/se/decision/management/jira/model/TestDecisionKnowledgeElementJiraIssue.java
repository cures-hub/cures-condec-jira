package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

public class TestDecisionKnowledgeElementJiraIssue {
	private int id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		this.id = 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";

		IssueType issueType = new MockIssueType(2, type.toString().toLowerCase(Locale.ENGLISH));

		Project project = new MockProject(1, projectKey);

		Issue issue = new MockIssue(id, "TEST-1");
		((MockIssue) issue).setProjectObject(project);
		((MockIssue) issue).setSummary(summary);
		((MockIssue) issue).setDescription(description);
		((MockIssue) issue).setIssueType(issueType);

		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
	}

	@Test
	public void testGetId() {
		assertEquals(this.id, this.decisionKnowledgeElement.getId(), 0.0);
	}

	@Test
	public void testGetName() {
		assertEquals(this.summary, this.decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals(this.description, this.decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(this.type, this.decisionKnowledgeElement.getType());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void testSetId() {
		this.decisionKnowledgeElement.setId(this.id + 1);
		assertEquals(this.id + 1, this.decisionKnowledgeElement.getId(), 0.0);
	}

	@Test
	public void testSetName() {
		this.decisionKnowledgeElement.setSummary(this.summary + "New");
		assertEquals(this.summary + "New", this.decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.decisionKnowledgeElement.setDescription(this.description + "New");
		assertEquals(this.description + "New", this.decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testSetType() {
		this.decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.decisionKnowledgeElement.getType());
	}

	@Test
	public void testSetProjectKey() {
		this.decisionKnowledgeElement.setProject(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.decisionKnowledgeElement.getProject().getProjectKey());
	}
}