package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestVisTimeLineNode extends TestSetUp {

	private KnowledgeElement element;
	private VisTimeLineNode timeNode;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
		timeNode = new VisTimeLineNode(element);
	}

	@Test
	public void testConstructorNull() {
		VisTimeLineNode node = new VisTimeLineNode(null);
		assertEquals(0, node.getId());
	}

	@Test
	public void testConstructorFilled() {
		VisTimeLineNode node = new VisTimeLineNode(element);
		assertEquals(element.getId(), node.getId());
	}

	@Test
	public void testConstructorWithGroup() {
		VisTimeLineNode node = new VisTimeLineNode(element, 123);
		assertEquals(123, node.getGroup());
	}

	@Test
	public void testGetId() {
		assertEquals(element.getId(), timeNode.getId());
	}

	@Test
	public void testSetId() {
		VisTimeLineNode node = new VisTimeLineNode(element);
		node.setId(12345);
		assertEquals(12345, node.getId());
	}

	@Test
	public void testGetContent() {
		element.setStatus(KnowledgeStatus.DECIDED);
		assertTrue(timeNode.getContent().startsWith("<img src="));
	}

	@Test
	public void testSetContent() {
		timeNode.setContent("Test new Content");
		assertEquals("Test new Content", timeNode.getContent());
	}

	@Test
	public void testSetGetStart() {
		Date date = new Date(System.currentTimeMillis() - 1000);
		String createdString = new SimpleDateFormat("yyyy-MM-dd").format(date);
		timeNode.setStart(createdString);
		assertEquals(createdString, timeNode.getStart());
	}

	@Test
	public void testGetClassName() {
		assertEquals(element.getTypeAsString().toLowerCase(), timeNode.getClassName());
	}

	@Test
	public void testSetClassName() {
		timeNode.setClassName("Test Class name");
		assertEquals("Test Class name", timeNode.getClassName());
	}

	@Test
	public void testGetAndSetGroup() {
		timeNode.setGroup(19012);
		assertEquals(19012, timeNode.getGroup());
	}

	@Test
	public void testGetAndSetTitle() {
		timeNode.setTitle("TestTitle");
		assertEquals("TestTitle", timeNode.getTitle());
	}
}
