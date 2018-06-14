package de.uhd.ifi.se.decision.documentation.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.documentation.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

/**
 * @description Test class for decision knowledge element getter and setter
 *              methods
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDecisionKnowledgeElementStrings extends TestSetUp {
	private EntityManager entityManager;
	private long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private String key;
	private DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		initialization();
		this.id = (long) 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "Test";
		this.key = "Test";

		this.decisionKnowledgeElement = new DecisionKnowledgeElementImpl(id, summary, description, type, projectKey,
				key);

		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testGetTypeAsString() {
		assertEquals("Solution", decisionKnowledgeElement.getType().toString());
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
		assertEquals(this.projectKey, this.decisionKnowledgeElement.getProjectKey());
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
		this.decisionKnowledgeElement.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.decisionKnowledgeElement.getProjectKey());
	}

	@Test
	public void testGetKeyKeyNull() {
		DecisionKnowledgeElement decisionKnowledgeElement = new DecisionKnowledgeElementImpl();
		decisionKnowledgeElement.setProjectKey("TEST");
		decisionKnowledgeElement.setId((long) 10);
		assertEquals("TEST-10", decisionKnowledgeElement.getKey());
	}

	@Test
	public void testDecisionKnowledgeElementInitialized() {
		assertNotNull(this.decisionKnowledgeElement);
	}

	@Test
	public void testEqualsNotTheObject() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId((long) 123);
		assertFalse(this.decisionKnowledgeElement.equals(element));
	}

	@Test
	public void testGetOutwardLinks() {
		decisionKnowledgeElement.getOutwardLinks();
	}

	@Test
	public void testGetInwardLinks() {
		decisionKnowledgeElement.getInwardLinks();
	}
}
