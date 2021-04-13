package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestArgument extends TestSetUp {

	private Argument argument;
	private String summary = "Increases usability";
	private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUETEXT;

	@Before
	public void setUp() {
		init();
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setType(KnowledgeType.PRO);
		knowledgeElement.setSummary(summary);
		knowledgeElement.setDocumentationLocation(documentationLocation);
		knowledgeElement.setProject("TEST");
		argument = new Argument(knowledgeElement);
	}

	@Test
	public void getArgumentProperties() {
		assertEquals(argument.getId(), 0);
		assertEquals(argument.getSummary(), summary);
		assertEquals(argument.getDocumentationLocation(), documentationLocation);
		assertEquals(argument.getType(), KnowledgeType.PRO);
		assertEquals(argument.getImage(), KnowledgeType.PRO.getIconUrl());
	}

	@Test
	public void testGetEmptyCriteria() {
		assertEquals(0, argument.getCriteria().size());
		assertNull(argument.getCriterion());
	}

	@Test
	public void testGetNonEmptyCriteria() {
		Argument argument = new Argument(KnowledgeElements.getProArgument());
		assertEquals(1, argument.getCriteria().size());
		assertEquals("NFR: Usability", argument.getCriterion().getSummary());
	}

	@Test
	public void testGetCriteriaTypes() {
		assertEquals(1, Argument.getCriteriaTypes("TEST").size());
		assertEquals("Non functional requirement", Argument.getCriteriaTypes("TEST").iterator().next());
	}

	@Test
	public void testConstructorWithLink() {
		Argument argument = new Argument(KnowledgeElements.getProArgument(), Links.getTestLink());
		// Link type "relates" has no effect, supports and attacks would change the
		// image to pro or con
		assertTrue(argument.getImage().contains("argument.png"));
	}
}
