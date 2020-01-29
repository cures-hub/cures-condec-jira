package de.uhd.ifi.se.decision.management.jira.view.treeviewer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestData extends TestSetUp {

	private KnowledgeElement element;
	private Data data;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
		data = new Data(element);
	}

	@Test
	public void testConstructorWithElementAndLink() {
		Link link = Links.getTestLink();
		Data data = new Data(element, link);
		assertEquals("tv1", data.getId());
	}

	@Test
	public void testGetId() {
		assertEquals("tv1", data.getId());
	}

	@Test
	public void testGetText() {
		assertEquals("WI: Implement feature", data.getText());
	}

	@Test
	public void testGetChildren() {
		assertEquals(new ArrayList<>(), data.getChildren());
	}

	@Test
	public void testGetElement() {
		assertEquals(element, data.getElement());
	}

	@Test
	public void testSetId() {
		data.setId("New");
		assertEquals("New", data.getId());
	}

	@Test
	public void testSetChildren() {
		List<Data> newChilden = new ArrayList<>();
		data.setChildren(newChilden);
		assertEquals(newChilden, data.getChildren());
	}

	@Test
	public void testGetIcon() {
		assertEquals(null, data.getIcon());
	}
}
