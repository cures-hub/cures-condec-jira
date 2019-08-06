package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

public class TestVisGraphNode extends TestSetUp {

	private DecisionKnowledgeElement element;
	private VisNode node;

	@Before
	public void setUp() {
		node = new VisNode();

		element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setKey("Test-1");
		element.setType("Argument");
		element.setDescription("Test");
		element.setProject("TEST");
		element.setSummary("TESTfwf");
		element.setDocumentationLocation("i");
	}

	@Test
	public void testConstructorNotCollapsedNoType() {
		this.node = new VisNode(element, "TEST", true, 1, 0);
		assertNotNull(node);
	}

	@Test
	public void testNodeSummary() {
		this.node = new VisNode(element, true, 1, 0);
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary();
		assertEquals(expectedLabel, this.node.getLabel());
	}

	@Test
	public void testNodeDescription() {
		this.node = new VisNode(element, true, 1, 0);
		String expectedTitle = "<b>" + element.getTypeAsString().toUpperCase() + " <br> " + element.getKey() + ":</b> "
				+ element.getSummary() + "<br> <i>" + element.getDescription() + "</i>";
		assertEquals(expectedTitle, this.node.getTitle());
	}

	@Test
	public void testNodeGroup() {
		this.node = new VisNode(element, true, 1, 0);
		assertEquals(element.getTypeAsString().toLowerCase(), this.node.getGroup());
	}

	@Test
	public void testNodeId() {
		this.node = new VisNode(element, true, 1, 0);
		String expectedId = element.getId() + "_" + element.getDocumentationLocationAsString();
		assertEquals(expectedId, this.node.getId());
	}

	@Test
	public void testLongSummary() {
		element.setSummary("Phasellus curabitur vestibulum aptent magna mattis odio mi vitae scelerisque scelerisque "
				+ "malesuada tristique libero molestie sapien dapibus vulputate.");
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary().substring(0, 99)
				+ "...";
		node = new VisNode(element, true, 1, 0);
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testCollapsed() {
		node = new VisNode(element, false, 1, 0);
		assertEquals("collapsed", node.getGroup());
	}

	@Test
	public void testGetLevel() {
		node = new VisNode(element, false, 1, 0);
		assertEquals(1, node.getLevel(), 0.0);
	}

	@Test
	public void testSetLevel() {
		node = new VisNode(element, false, 1, 0);
		node.setLevel(20);
		assertEquals(20, node.getLevel(), 0.0);
	}

	@Test
	public void testGetCid() {
		node = new VisNode(element, false, 1, 12);
		assertEquals(12, node.getCid(), 0.0);
	}

	@Test
	public void testSetCid() {
		node = new VisNode(element, false, 1, 0);
		node.setCid(22);
		assertEquals(22, node.getCid(), 0.0);
	}

}
