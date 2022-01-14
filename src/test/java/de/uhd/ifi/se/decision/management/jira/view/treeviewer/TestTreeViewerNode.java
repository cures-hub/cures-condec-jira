package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestTreeViewerNode extends TestSetUp {

	private KnowledgeElement element;
	private TreeViewerNode node;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
		node = new TreeViewerNode(element, new FilterSettings());
	}

	@Test
	public void testConstructorWithElementAndLink() {
		Link link = Links.getTestLink();
		TreeViewerNode node = new TreeViewerNode(element, link, new FilterSettings());
		assertEquals("tv1", node.getId());
	}

	@Test
	public void testConstructorWithDescNull() {
		element.setDescription(null);
		TreeViewerNode node = new TreeViewerNode(element, new FilterSettings());
		assertNotNull(node);
		element.setDescription("TestDescription");
	}

	@Test
	public void testConstructorWithDescBlank() {
		element.setDescription("");
		TreeViewerNode node = new TreeViewerNode(element, new FilterSettings());
		assertNotNull(node);
		element.setDescription("TestDescription");
	}

	@Test
	public void testConstructorWithDescUndefined() {
		element.setDescription("undefined");
		TreeViewerNode node = new TreeViewerNode(element, new FilterSettings());
		assertNotNull(node);
		element.setDescription("TestDescription");
	}

	@Test
	public void testConstructorWithColorNode() {
		node = new TreeViewerNode(element, new FilterSettings());
		assertNotNull(node);
	}

	@Test
	public void testGetId() {
		assertEquals("tv1", node.getId());
	}

	@Test
	public void testGetText() {
		assertEquals("WI: Implement feature", node.getText());
	}

	@Test
	public void testGetChildren() {
		assertEquals(new ArrayList<>(), node.getChildren());
	}

	@Test
	public void testGetElement() {
		assertEquals(element, node.getElement());
	}

	@Test
	public void testSetId() {
		node.setId("New");
		assertEquals("New", node.getId());
	}

	@Test
	public void testSetChildren() {
		List<TreeViewerNode> newChilden = new ArrayList<>();
		node.setChildren(newChilden);
		assertEquals(newChilden, node.getChildren());
	}

	@Test
	public void testGetIcon() {
		assertEquals(null, node.getIcon());
	}

	@Test
	public void testGetAndSetAttributes() {
		Map<String, String> attributes = node.getAttr();
		assertEquals(2L, attributes.size());
		assertTrue(attributes.get("title").contains("Only 1 decision is reached."));
		assertTrue(attributes.get("title").contains("Linked decision knowledge is incomplete."));
		assertEquals("color:crimson", attributes.get("style"));
		Map<String, String> newAttributes = new HashMap<>();
		newAttributes.put("title", "test");
		node.setAttr(newAttributes);
		attributes = node.getAttr();
		assertEquals(attributes.size(), 1L);
		assertEquals(attributes.get("title"), "test");
	}

	@Test
	public void testGetAndSetLiAttributes() {
		Map<String, String> attributes = node.getLiAttr();
		assertEquals(attributes.size(), 1L);
		assertEquals(attributes.get("class"), "issue");
		Map<String, String> newAttributes = new HashMap<>();
		newAttributes.put("class", "test");
		newAttributes.put("style", "color:crimson");
		node.setLiAttr(newAttributes);
		attributes = node.getLiAttr();
		assertEquals(attributes.size(), 2L);
		assertEquals(attributes.get("class"), "test");
		assertEquals(attributes.get("style"), "color:crimson");
	}
}
