package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestSolutionOption extends TestSetUp {

	private SolutionOption alternative;
	final private String summary = "We could use a NoSQL database!";
	final private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUETEXT;

	@Before
	public void setUp() {
		init();
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setProject("TEST");
		knowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		knowledgeElement.setSummary(summary);
		knowledgeElement.setDocumentationLocation(documentationLocation);
		alternative = new SolutionOption(knowledgeElement);
	}

	@Test
	public void testGetAlternativeProperties() {
		assertEquals(0, alternative.getId());
		assertEquals(summary, alternative.getSummary());
		assertEquals(documentationLocation, alternative.getDocumentationLocation());
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(KnowledgeType.ALTERNATIVE.getIconUrl(), alternative.getImage());
	}

	@Test
	public void testGetEmptyArguments() {
		assertEquals(0, alternative.getArguments().size());
	}

	@Test
	public void testGetNonEmptyArguments() {
		alternative = new SolutionOption(KnowledgeElements.getAlternative());
		assertEquals(1, alternative.getArguments().size());
	}
}
