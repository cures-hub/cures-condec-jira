package de.uhd.ifi.se.decision.management.jira.view.vis;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestVisNode extends TestSetUpWithIssues {

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
		this.node = new VisNode(element,"TEST",true,1,0);
		assertNotNull(node);
	}

	@Test
	public void testNodeSummary() {
		this.node = new VisNode(element,true,1,0);
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n" + element.getSummary();
		assertEquals(expectedLabel, this.node.getLabel());
	}

	@Test
	public void testNodeDescription() {
		this.node = new VisNode(element,true,1,0);
		String expectedTitle = "<b>" + element.getTypeAsString().toUpperCase() + " <br> " +
				element.getKey() + ":</b> " + element.getSummary() +"<br> <i>" + element.getDescription() +"</i>";
		assertEquals(expectedTitle, this.node.getTitle());
	}

	@Test
	public void testNodeGroup() {
		this.node = new VisNode(element,true,1,0);
		assertEquals(element.getTypeAsString().toLowerCase(), this.node.getGroup());
	}

	@Test
	public void testNodeId() {
		this.node = new VisNode(element, true,1,0);
		String expectedId = element.getId()+ "_" + element.getDocumentationLocationAsString();
		assertEquals(expectedId, this.node.getId());
	}

	@Test
	public void testLongSummary() {
		element.setSummary("Phasellus curabitur vestibulum aptent magna mattis odio mi vitae scelerisque scelerisque " +
				"malesuada tristique libero molestie sapien dapibus vulputate.");
		String expectedLabel = element.getTypeAsString().toUpperCase() + "\n"
				+ element.getSummary().substring(0,99) + "...";
		node = new VisNode(element, true, 1, 0);
		assertEquals(expectedLabel, node.getLabel());
	}

	@Test
	public void testCollapsed() {
		node = new VisNode(element, false, 1, 0);
		assertEquals("collapsed", node.getGroup());
	}
}
