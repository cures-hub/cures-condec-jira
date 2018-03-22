package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestNodeInfo {

	private String id;
	private String key;
	private String issueType;
	private String description;
	private String summary;

	private NodeInfo node;

	@Before
	public void setUp() {
		node = new NodeInfo();
		id = "Test";
		key = "Test";
		issueType = "Test";
		description = "Test";
		summary = "Test";
		node.setDescription(description);
		node.setId(id);
		node.setType(issueType);
		node.setKey(key);
		node.setSummary(summary);
	}

	@Test
	public void testGetId() {
		assertEquals(id, node.getId());
	}

	@Test
	public void testGetKey() {
		assertEquals(key, node.getKey());
	}

	@Test
	public void testGetIssueType() {
		assertEquals(issueType, node.getType());
	}

	@Test
	public void testGetDescription() {
		assertEquals(description, node.getDescription());
	}

	@Test
	public void testGetSummary() {
		assertEquals(summary, node.getSummary());
	}

	@Test
	public void testsetId() {
		node.setId(id + "New");
		assertEquals(id + "New", node.getId());
	}

	@Test
	public void testSetKey() {
		node.setKey(key + "New");
		assertEquals(key + "New", node.getKey());
	}

	@Test
	public void testSetIssueType() {
		node.setType(issueType + "New");
		assertEquals(issueType + "New", node.getType());
	}

	@Test
	public void testSetDescription() {
		node.setDescription(description + "New");
		assertEquals(description + "New", node.getDescription());
	}

	@Test
	public void testSetSummary() {
		node.setSummary(summary + "New");
		assertEquals(summary + "New", node.getSummary());
	}
}