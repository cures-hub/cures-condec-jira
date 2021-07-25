package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestVisGraphEdge extends TestSetUp {

	private VisEdge edge;
	private Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLink();
		edge = new VisEdge(link);
	}

	@Test
	public void testConstructor() {
		assertNotNull(edge);
	}

	@Test
	public void testId() {
		assertEquals((link.getSource().getId() + "_i" + link.getTarget().getId() + "_i").hashCode(), edge.getId());
	}

	@Test
	public void testLabel() {
		assertEquals(link.getTypeAsString(), edge.getLabel());
	}

	@Test
	public void testFrom() {
		String expected = link.getSource().getId() + "_" + link.getSource().getDocumentationLocationAsString();
		assertEquals(expected, edge.getFrom());
	}

	@Test
	public void testTo() {
		String expected = link.getTarget().getId() + "_" + link.getTarget().getDocumentationLocationAsString();
		assertEquals(expected, edge.getTo());
	}

	@Test
	public void testColor() {
		String expected = "#80c9ff";
		assertEquals(expected, edge.getColor());
	}
}
