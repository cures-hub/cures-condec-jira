package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;

public class TestNode {
	private Node node;

	@Before
	public void setUp() {
		this.node = new DecisionKnowledgeElementImpl(12, "TEST", DocumentationLocation.JIRAISSUE.getIdentifier());
	}

	@Test
	public void testGetId() {
		assertEquals(12, this.node.getId());
	}

	@Test
	public void testGetDocumentationLocation() {
		assertEquals(DocumentationLocation.JIRAISSUE, this.node.getDocumentationLocation());
	}

	@Test
	public void testEqualsNull() {
		assertFalse(this.node.equals(null));
	}

	@Test
	public void testEqualsSame() {
		assertTrue(this.node.equals(this.node));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsOtherObject() {
		assertFalse(this.node.equals(new LinkImpl()));
	}

	@Test
	public void testEqualsEquals() {
		Node nodeEquals = new DecisionKnowledgeElementImpl(12, "TEST", DocumentationLocation.JIRAISSUE.getIdentifier());
		assertTrue(this.node.equals(nodeEquals));
	}
}
