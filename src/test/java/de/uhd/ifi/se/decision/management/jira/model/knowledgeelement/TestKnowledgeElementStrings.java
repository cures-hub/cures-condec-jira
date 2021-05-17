package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;

/**
 * Test class for decision knowledge element getter and setter methods
 */
public class TestKnowledgeElementStrings extends TestSetUp {
	private long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		this.id = 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup", this.decisionKnowledgeElement);
	}

	@Test
	public void testGetTypeAsString() {
		assertEquals("Solution", decisionKnowledgeElement.getType().toString());
	}

	@Test
	public void testGetTypeOther() {
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, "Work-Item",
				projectKey, "TEST-1", DocumentationLocation.JIRAISSUE, "");
		assertEquals(KnowledgeType.OTHER, decisionKnowledgeElement.getType());
	}

	@Test
	public void testGetId() {
		assertEquals(this.id, this.decisionKnowledgeElement.getId());
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
		assertEquals(this.id + 1, this.decisionKnowledgeElement.getId());
	}

	@Test
	public void testSetSummary() {
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
		this.decisionKnowledgeElement.setProject("TEST");
		assertEquals("TEST", this.decisionKnowledgeElement.getProject().getProjectKey());
	}

	@Test
	public void testGetKeyKeyNull() {
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement();
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setId(10);
		assertEquals("TEST-10", decisionKnowledgeElement.getKey());
	}

	@Test
	public void testDecisionKnowledgeElementInitialized() {
		assertNotNull(this.decisionKnowledgeElement);
	}

	@Test
	public void testEqualsNotTheObject() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(123);
		assertFalse(this.decisionKnowledgeElement.equals(element));
	}

	@Test
	public void testGetDocumentationLocationAsStringNull() {
		this.decisionKnowledgeElement.setDocumentationLocation((DocumentationLocation) null);
		assertEquals("", decisionKnowledgeElement.getDocumentationLocationAsString());
	}

	@Test
	public void isLinked() {
		assertEquals(0, decisionKnowledgeElement.isLinked());
	}

	@Test
	public void testGetDecisionGroups() {
		List<String> groups = decisionKnowledgeElement.getDecisionGroups();
		assertEquals("TestGroup", groups.get(0));
	}

	@Test
	public void testAddDecisionGroupSingle() {
		String group = "NewTestGroup";
		decisionKnowledgeElement.addDecisionGroup(group);
		assertTrue(decisionKnowledgeElement.getDecisionGroups().contains("NewTestGroup"));
	}

	@Test
	public void testAddDecisionGroupsList() {
		List<String> groups = new ArrayList<String>();
		groups.add("ListTestGroup");
		decisionKnowledgeElement.addDecisionGroups(groups);
		assertTrue(decisionKnowledgeElement.getDecisionGroups().contains("ListTestGroup"));
	}

	@Test
	public void testRemoveDecisionGroup() {
		decisionKnowledgeElement.removeDecisionGroup("TestGroup");
		assertFalse(decisionKnowledgeElement.getDecisionGroups().contains("TestGroup"));
	}
}
