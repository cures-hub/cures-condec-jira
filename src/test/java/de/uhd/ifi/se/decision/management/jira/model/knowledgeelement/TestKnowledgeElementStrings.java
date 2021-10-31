package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

/**
 * Test class for decision knowledge element getter and setter methods
 */
public class TestKnowledgeElementStrings extends TestSetUp {
	private long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		this.id = 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";
		String key = "Test";

		this.element = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
	}

	@Test
	public void testGetTypeAsString() {
		assertEquals("Solution", element.getType().toString());
	}

	@Test
	public void testGetTypeOther() {
		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, "Work-Item",
				projectKey, "TEST-1", DocumentationLocation.JIRAISSUE, "");
		assertEquals(KnowledgeType.OTHER, decisionKnowledgeElement.getType());
	}

	@Test
	public void testGetId() {
		assertEquals(this.id, this.element.getId());
	}

	@Test
	public void testGetName() {
		assertEquals(this.summary, this.element.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals(this.description, this.element.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(this.type, this.element.getType());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.element.getProject().getProjectKey());
	}

	@Test
	public void testSetId() {
		this.element.setId(this.id + 1);
		assertEquals(this.id + 1, this.element.getId());
	}

	@Test
	public void testSetSummary() {
		this.element.setSummary(this.summary + "New");
		assertEquals(this.summary + "New", this.element.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.element.setDescription(this.description + "New");
		assertEquals(this.description + "New", this.element.getDescription());
	}

	@Test
	public void testSetType() {
		this.element.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.element.getType());
	}

	@Test
	public void testSetProjectKey() {
		this.element.setProject("TEST");
		assertEquals("TEST", this.element.getProject().getProjectKey());
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
		assertNotNull(this.element);
	}

	@Test
	public void testEqualsNotTheObject() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(123);
		assertFalse(this.element.equals(element));
	}

	@Test
	public void testGetDocumentationLocationAsStringNull() {
		this.element.setDocumentationLocation((DocumentationLocation) null);
		assertEquals("", element.getDocumentationLocationAsString());
	}

	@Test
	public void testGetOrigin() {
		assertEquals(Origin.DOCUMENTATION_LOCATION, element.getOrigin());
	}

	@Test
	public void isLinked() {
		assertEquals(0, element.isLinked());
	}

	@Test
	@NonTransactional
	public void testGetDecisionGroups() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
		DecisionGroupPersistenceManager.insertGroup("High_Level", element);
		List<String> groups = element.getDecisionGroups();
		assertEquals("High_Level", groups.get(0));
		assertEquals("TestGroup", groups.get(1));
	}
}
