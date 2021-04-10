package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestArgument extends TestSetUp {

	private Argument argument;
	final private String summary = "Increases usability";
	final private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	final private String criterionSummary = "NFR: Usability";
	final private DocumentationLocation criterionDocumentationLocation = DocumentationLocation.JIRAISSUE;

	@Before
	public void setUp() {
		init();
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setType(KnowledgeType.PRO);
		knowledgeElement.setSummary(summary);
		knowledgeElement.setDocumentationLocation(documentationLocation);
		knowledgeElement.setProject("TEST");
		this.argument = new Argument(knowledgeElement);
	}

	@Test
	public void getArgumentProperties() {
		assertEquals(this.argument.getId(), 0);
		assertEquals(this.argument.getSummary(), summary);
		assertEquals(this.argument.getDocumentationLocation(), documentationLocation);
		assertEquals(this.argument.getType(), KnowledgeType.PRO);
		assertEquals(this.argument.getImage(), KnowledgeType.PRO.getIconUrl());
	}

	@Test
	public void testGetEmptyCriteria() {
		assertEquals(0, this.argument.getCriteria().size());
	}

	@Test
	public void testGetCriteriaTypes() {
		assertEquals(1, this.argument.getCriteriaTypes().size());
		assertEquals("Non functional requirement", this.argument.getCriteriaTypes().iterator().next());
	}
}
