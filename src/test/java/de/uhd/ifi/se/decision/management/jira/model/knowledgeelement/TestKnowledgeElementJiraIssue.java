package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;

public class TestKnowledgeElementJiraIssue extends TestSetUp {
	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		Issue issue = JiraIssues.getTestJiraIssues().get(0);
		decisionKnowledgeElement = new KnowledgeElement(issue);
	}

	@Test
	public void testGetId() {
		assertEquals(1, this.decisionKnowledgeElement.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("WI: Implement feature", this.decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals("WI: Implement feature", this.decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(KnowledgeType.OTHER, this.decisionKnowledgeElement.getType());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals("TEST", this.decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void testSetId() {
		this.decisionKnowledgeElement.setId(2);
		assertEquals(2, this.decisionKnowledgeElement.getId());
	}

	@Test
	public void testSetName() {
		this.decisionKnowledgeElement.setSummary("WI: Do something else");
		assertEquals("WI: Do something else", this.decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.decisionKnowledgeElement.setDescription("WI: Do something else");
		assertEquals("WI: Do something else", this.decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testSetType() {
		this.decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.decisionKnowledgeElement.getType());
	}

	@Test
	public void testSetProjectKey() {
		this.decisionKnowledgeElement.setProject("TEST");
		assertEquals("TEST", this.decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void isLinked() {
		assertEquals(9, decisionKnowledgeElement.isLinked());
	}
}