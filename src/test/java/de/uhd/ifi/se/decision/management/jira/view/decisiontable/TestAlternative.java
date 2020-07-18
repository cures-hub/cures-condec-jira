package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestAlternative extends TestSetUp {

	private Alternative alternative;
	final private String projectKey = "TEST";
	final private String summary = "Do it this way!";
	final private String documentationLocation = "s";
	
	@Before
	public void setUp() {
		init();
		KnowledgeElement keAlternative = new KnowledgeElement();
		keAlternative.setProject(projectKey);
		keAlternative.setType(KnowledgeType.ALTERNATIVE);
		keAlternative.setSummary(summary);
		keAlternative.setDocumentationLocation(documentationLocation);
		this.alternative = new Alternative(keAlternative);	
	}
	
	@Test
	public void testGetEmptyArguments() {
		assertEquals(this.alternative.getArguments().size(), 0);
	}
	
	@Test
	public void testGetArguments() {
		KnowledgeElement keArgument = new KnowledgeElement();
		keArgument.setProject(projectKey);
		keArgument.setType(KnowledgeType.PRO);
		Argument argument = new Argument(keArgument);
		this.alternative.addArgument(argument);
		assertEquals(this.alternative.getArguments().size(), 1);
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
