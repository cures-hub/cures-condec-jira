package de.uhd.ifi.se.decision.management.jira.view.vis;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestVisGraphNode extends TestSetUp {

	private static KnowledgeElement element;

	@BeforeClass
	public static void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testNodeSummary() {
		VisNode node = new VisNode(element, false, 1);
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary();
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testNodeDescription() {
		VisNode node = new VisNode(element, true, 1);
		String expectedTitle = "<b>" + element.getTypeAsString().toUpperCase() + " <br> " + element.getKey() + ":</b> "
				+ element.getSummary() + "<br> <i>" + element.getDescription() + "</i>";
		assertEquals(expectedTitle, node.getTitle());
	}

	@Test
	public void testNodeGroup() {
		VisNode node = new VisNode(element, false, 1);
		assertEquals(element.getTypeAsString().toLowerCase(), node.getGroup());
	}

	@Test
	public void testNodeId() {
		VisNode node = new VisNode(element, true, 1);
		String expectedId = element.getId() + "_" + element.getDocumentationLocationAsString();
		assertEquals(expectedId, node.getVisNodeId());
		assertEquals(element.getId(), node.getElementId());
		assertEquals(element.getDocumentationLocationAsString(), node.getDocumentationLocation());
	}

	@Test
	public void testLongSummary() {
		element.setSummary("Phasellus curabitur vestibulum aptent magna mattis odio mi vitae scelerisque scelerisque "
				+ "malesuada ...");
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary().substring(0, 99)
				+ "...";
		VisNode node = new VisNode(element, false, 1);
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testCollapsed() {
		VisNode node = new VisNode(element, true, 1);
		assertEquals("collapsed", node.getGroup());
	}

	@Test
	public void testGetLevel() {
		VisNode node = new VisNode(element, false, 1);
		assertEquals(1, node.getLevel());
	}

	@Test
	public void testGetFont() {
		VisNode node = new VisNode(element, false, 1);
		assertEquals("crimson", node.getFont().values().iterator().next());
	}

	@Test
	public void testGetFontProjectNull() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setProject((DecisionKnowledgeProject) null);
		VisNode node = new VisNode(element, false, 1);
		assertEquals("crimson", node.getFont().values().iterator().next());
	}

	@Test
	public void testGetColor() {
		VisNode node = new VisNode(element, false, 1);
		assertEquals("#ffffff", node.getColor());
		assertEquals("#ffffff", node.getColorMap().get("background"));
	}

	@Test
	public void testGetAndSetKnowledgeElement() {
		KnowledgeElement element = new KnowledgeElement();
		VisNode node = new VisNode(element, false, 1);
		assertEquals(node.getElement(), element);
		KnowledgeElement newElement = new KnowledgeElement();
		node.setElement(newElement);
		assertEquals(node.getElement(), newElement);
	}
}