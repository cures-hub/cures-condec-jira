package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestAlternative extends TestSetUp {

	private Alternative alternative;
	final private String projectKey = "TEST";
	final private String summary = "Do it this way!";
	final private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUETEXT;

	@Before
	public void setUp() {
		init();
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setProject(projectKey);
		knowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		knowledgeElement.setSummary(summary);
		knowledgeElement.setDocumentationLocation(documentationLocation);
		this.alternative = new Alternative(knowledgeElement);
	}

	@Test
	public void testGetEmptyArguments() {
		assertEquals(this.alternative.getArguments().size(), 0);
		assertEquals(this.alternative.getImage(), KnowledgeType.ALTERNATIVE.getIconUrl());
	}

	@Test
	public void testGetArguments() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setProject(projectKey);
		knowledgeElement.setType(KnowledgeType.PRO);

		Argument argument = new Argument(knowledgeElement);
		this.alternative.addArgument(argument);
		assertEquals(this.alternative.getArguments().size(), 1);
		assertEquals(this.alternative.getImage(), KnowledgeType.ALTERNATIVE.getIconUrl());
		assertEquals(this.alternative.getArguments().get(0).getImage(), KnowledgeType.PRO.getIconUrl());

	}

	@Test
	public void testGetAlternativeId() {
		this.alternative.getArguments().clear();
		assertEquals(0, this.alternative.getId());
	}

	@Test
	public void testGetAlternativeSummary() {
		assertEquals(summary, this.alternative.getSummary());
	}

	@Test
	public void testGetAlternativeDocumentationLocation() {
		assertEquals(documentationLocation, this.alternative.getDocumentationLocation());
	}
}
