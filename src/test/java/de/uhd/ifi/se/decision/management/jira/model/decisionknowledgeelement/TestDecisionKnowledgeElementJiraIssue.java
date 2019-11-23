package de.uhd.ifi.se.decision.management.jira.model.decisionknowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestDecisionKnowledgeElementJiraIssue extends TestSetUp {
	private int id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		this.id = 1;
		this.summary = "WI: Implement feature";
		this.description = "WI: Implement feature";
		this.type = KnowledgeType.OTHER;
		this.projectKey = "TEST";

		Issue issue = JiraIssues.getTestJiraIssues().get(0);

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

	@Test
	public void isLinked() {
		assertEquals(9, decisionKnowledgeElement.isLinked());
	}
}