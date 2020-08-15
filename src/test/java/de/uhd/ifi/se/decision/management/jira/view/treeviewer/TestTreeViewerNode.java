package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
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
		node = new TreeViewerNode(element);
	}

	@Test
	public void testConstructorWithElementAndLink() {
		Link link = Links.getTestLink();
		TreeViewerNode node = new TreeViewerNode(element, link);
		assertEquals("tv1", node.getId());
	}

	@Test
	public void testConstructorWithDescNull() {
		element.setDescription(null);
		TreeViewerNode node = new TreeViewerNode(element);
		assertNotNull(node);
		element.setDescription("TestDescription");
	}

	@Test
	public void testConstructorWithDescBlank() {
		element.setDescription("");
		TreeViewerNode node = new TreeViewerNode(element);
		assertNotNull(node);
		element.setDescription("TestDescription");
	}

	@Test
	public void testConstructorWithDescUndefined() {
		element.setDescription("undefined");
		TreeViewerNode node = new TreeViewerNode(element);
		assertNotNull(node);
		element.setDescription("TestDescription");
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
}
