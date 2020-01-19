package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;

public class TestData {
	private String id;
	private String text;
	private List<Data> children;
	private KnowledgeElement nodeInfo;

	private Data data;

	@Before
	public void setUp() {
		id = "Test";
		text = "Test";
		children = new ArrayList<>();
		nodeInfo = new KnowledgeElementImpl();
		data = new Data();
		data.setChildren(children);
		data.setId(id);
		data.setNodeInfo(nodeInfo);
		data.setText(text);
	}

	@Test
	public void testGetId() {
		assertEquals(data.getId(), id);
	}

	@Test
	public void testGetText() {
		assertEquals(data.getText(), text);
	}

	@Test
	public void testGetChildren() {
		assertEquals(data.getChildren(), children);
	}

	@Test
	public void testGetNodeInfo() {
		assertEquals(data.getNode(), nodeInfo);
	}

	@Test
	public void testSetId() {
		data.setId(id + "New");
		assertEquals(data.getId(), id + "New");
	}

	@Test
	public void testSetText() {
		data.setText(text + "New");
		assertEquals(data.getText(), text + "New");
	}

	@Test
	public void testSetChildren() {
		List<Data> newChilden = new ArrayList<>();
		data.setChildren(newChilden);
		assertEquals(newChilden, data.getChildren());
	}

	@Test
	public void testSetNodeInfo() {
		KnowledgeElementImpl newInfo = new KnowledgeElementImpl();
		data.setNodeInfo(newInfo);
		assertEquals(newInfo, data.getNode());
	}
}
