package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;

/**
 *
 * @author Tim Kuchenbuch
 * @description Test Class for Simple Geter and Setter Tests
 *
 */
public class TestDecisionKnowledgeElementStrings {

	private Long id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private String key;
	private DecisionKnowledgeElement repre;

	@Before
	public void setUp() {
		this.id = (long) 100;
		this.summary = "Test";
		this.description = "Test";
		this.type = KnowledgeType.SOLUTION;
		this.projectKey = "Test";
		this.key = "Test";

		this.repre = new DecisionKnowledgeElement(id, summary, description, type, projectKey, key);
	}

	@Test
	public void testgetId() {
		assertEquals(this.id, this.repre.getId(), 0.0);
	}

	@Test
	public void testGetName() {
		assertEquals(this.summary, this.repre.getSummary());
	}

	@Test
	public void testGetDescription() {
		assertEquals(this.description, this.repre.getDescription());
	}

	@Test
	public void testGetType() {
		assertEquals(this.type, this.repre.getType());
	}

	@Test
	public void testGetPKey() {
		assertEquals(this.projectKey, this.repre.getProjectKey());
	}

	@Test
	public void testSetId() {
		this.repre.setId(this.id + 1);
		assertEquals(this.id + 1, this.repre.getId(), 0.0);
	}

	@Test
	public void testSetName() {
		this.repre.setSummary(this.summary + "New");
		assertEquals(this.summary + "New", this.repre.getSummary());
	}

	@Test
	public void testSetDescription() {
		this.repre.setDescription(this.description + "New");
		assertEquals(this.description + "New", this.repre.getDescription());
	}

	@Test
	public void testSetType() {
		this.repre.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.repre.getType());
	}

	@Test
	public void tstSetPKey() {
		this.repre.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.repre.getProjectKey());
	}
}
