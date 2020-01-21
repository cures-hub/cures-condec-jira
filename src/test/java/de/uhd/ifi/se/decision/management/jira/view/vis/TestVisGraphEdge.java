package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;

public class TestVisGraphEdge extends TestSetUp {

	private VisEdge edge;
	private Link link;

	@Before
	public void setUp() {
		KnowledgeElement from = new KnowledgeElementImpl();
		from.setId(1);
		from.setKey("Test-1");
		from.setType("Argument");
		from.setDescription("Test");
		from.setProject("TEST");
		from.setSummary("TESTfwf");
		from.setDocumentationLocation("i");

		KnowledgeElement to = new KnowledgeElementImpl();
		to.setId(1);
		to.setKey("Test-1");
		to.setType("Argument");
		to.setDescription("Test");
		to.setProject("TEST");
		to.setSummary("TESTfwf");
		to.setDocumentationLocation("i");

		link = new LinkImpl(from, to);
		link.setType("test");
		link.setId(1);
	}

	@Test
	public void testConstructor() {
		edge = new VisEdge(link);
		assertNotNull(edge);
	}

	@Test
	public void testId() {
		edge = new VisEdge(link);
		assertEquals(String.valueOf(link.getId()), edge.getId());
	}

	@Test
	public void testLabel() {
		edge = new VisEdge(link);
		assertEquals(link.getType(), edge.getLabel());
	}

	@Test
	public void testFrom() {
		edge = new VisEdge(link);
		String expected = link.getSource().getId() + "_" + link.getSource().getDocumentationLocationAsString();
		assertEquals(expected, edge.getFrom());
	}

	@Test
	public void testTo() {
		edge = new VisEdge(link);
		String expected = link.getTarget().getId() + "_" + link.getTarget().getDocumentationLocationAsString();
		assertEquals(expected, edge.getTo());
	}
}
