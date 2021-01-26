package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

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
		assertEquals(1, decisionKnowledgeElement.getId());
	}

	@Test
	public void testGetName() {
		assertEquals("WI: Implement feature", decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals("WI: Implement feature", decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(KnowledgeType.OTHER, decisionKnowledgeElement.getType());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals("TEST", decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void testSetId() {
		this.decisionKnowledgeElement.setId(2);
		assertEquals(2, decisionKnowledgeElement.getId());
	}

	@Test
	public void testSetName() {
		this.decisionKnowledgeElement.setSummary("WI: Do something else");
		assertEquals("WI: Do something else", decisionKnowledgeElement.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.decisionKnowledgeElement.setDescription("WI: Do something else");
		assertEquals("WI: Do something else", decisionKnowledgeElement.getDescription());
	}

	@Test
	public void testSetType() {
		this.decisionKnowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, decisionKnowledgeElement.getType());
	}

	@Test
	public void testSetProjectKey() {
		this.decisionKnowledgeElement.setProject("TEST");
		assertEquals("TEST", decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void testIsLinked() {
		assertEquals(17, decisionKnowledgeElement.isLinked());
	}

	@Test
	public void testGetCreationDate() {
		decisionKnowledgeElement.setCreationDate(null);
		assertNotNull(decisionKnowledgeElement.getCreationDate());
	}

	@Test
	public void testGetUpdatingDate() {
		decisionKnowledgeElement.setUpdatingDate(null);
		assertNotNull(decisionKnowledgeElement.getUpdatingDate());

		decisionKnowledgeElement.setUpdatingDate(new Date());
		assertNotNull(decisionKnowledgeElement.getUpdatingDate());
	}
}