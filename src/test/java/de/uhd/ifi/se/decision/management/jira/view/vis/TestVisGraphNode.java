package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestVisGraphNode extends TestSetUp {

	private static KnowledgeElement element;

	@BeforeClass
	public static void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testNodeSummary() {
		VisNode node = new VisNode(element, false, 1, 0);
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary();
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testNodeDescription() {
		VisNode node = new VisNode(element, true, 1, 0);
		String expectedTitle = "<b>" + element.getTypeAsString().toUpperCase() + " <br> " + element.getKey() + ":</b> "
				+ element.getSummary() + "<br> <i>" + element.getDescription() + "</i>";
		assertEquals(expectedTitle, node.getTitle());
	}

	@Test
	public void testNodeGroup() {
		VisNode node = new VisNode(element, false, 1, 0);
		assertEquals(element.getTypeAsString().toLowerCase(), node.getGroup());
	}

	@Test
	public void testNodeId() {
		VisNode node = new VisNode(element, true, 1, 0);
		String expectedId = element.getId() + "_" + element.getDocumentationLocationAsString();
		assertEquals(expectedId, node.getId());
	}

	@Test
	public void testLongSummary() {
		element.setSummary("Phasellus curabitur vestibulum aptent magna mattis odio mi vitae scelerisque scelerisque "
				+ "malesuada ...");
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary().substring(0, 99)
				+ "...";
		VisNode node = new VisNode(element, false, 1, 0);
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testCollapsed() {
		VisNode node = new VisNode(element, true, 1, 0);
		assertEquals("collapsed", node.getGroup());
	}

	@Test
	public void testGetLevel() {
		VisNode node = new VisNode(element, false, 1, 0);
		assertEquals(1, node.getLevel());
	}

	@Test
	public void testGetCid() {
		VisNode node = new VisNode(element, false, 1, 12);
		assertEquals(12, node.getCid());
	}

	@Test
	public void testGetFont() {
		element.setStatus(KnowledgeStatus.DISCARDED);
		VisNode node = new VisNode(element, false, 1, 12);
		assertEquals("gray", node.getFont().values().iterator().next());
		element.setStatus(KnowledgeStatus.UNDEFINED);
	}
}