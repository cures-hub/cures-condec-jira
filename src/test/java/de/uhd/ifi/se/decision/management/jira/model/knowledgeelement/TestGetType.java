package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetType extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testValidElementWithDocumentationLocationJiraIssue() {
		// Jira issue with task Jira issue type
		assertEquals(KnowledgeType.OTHER, element.getType());
		assertEquals("Task", element.getTypeAsString());
	}

	@Test
	public void testNonExistingElementGetTypeAsString() {
		KnowledgeElement element = new KnowledgeElement();
		element.setKey("NOTTEST-999");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertEquals("", element.getTypeAsString());
	}

	@Test
	public void testElementWithTypeNull() {
		KnowledgeElement element = new KnowledgeElement();
		element.setType((KnowledgeType) null);
		assertEquals(KnowledgeType.OTHER, element.getType());
	}

	@Test
	public void testUpdateKnowledgeTypeAndStatus() {
		KnowledgeElement newElement = new KnowledgeElement();
		newElement.setType(KnowledgeType.DECISION);
		assertEquals(KnowledgeStatus.DECIDED, newElement.getStatus());

		newElement.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeStatus.REJECTED, newElement.getStatus());
		assertEquals(KnowledgeType.DECISION, newElement.getType());
	}
}