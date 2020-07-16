package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestArgument extends TestSetUp {

	private Argument argument;
	final private String projectKey = "TEST";
	final private String summary = "Increases usability";
	final private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	final private String criterionSummary = "NFR: Usability";
	final private DocumentationLocation criterionDocumentationLocation = DocumentationLocation.JIRAISSUE;

	@Before
	public void setUp() {
		init();
		KnowledgeElement keArgument = new KnowledgeElement();
		keArgument.setType(KnowledgeType.PRO);
		keArgument.setSummary(summary);
		keArgument.setDocumentationLocation(documentationLocation);
		this.argument = new Argument(keArgument);	
	}
	
	@Test
	public void getArgumentProperties() {
		assertEquals(this.argument.getId(), 0);
		assertEquals(this.argument.getSummary(), summary);
		assertEquals(this.argument.getDocumentationLocation(), documentationLocation.getIdentifier());
		assertEquals(this.argument.getType(), KnowledgeType.PRO.toString());
	}
	
	@Test
	public void testGetEmptyCriterion() {
		assertEquals(this.argument.getCriterion(), null);
	}
	
	@Test
	public void testGetCriterion() {
		KnowledgeElement keCriterion = new KnowledgeElement();
		keCriterion.setProject(projectKey);
		keCriterion.setType(KnowledgeType.OTHER);
		keCriterion.setSummary(criterionSummary);
		keCriterion.setDocumentationLocation(criterionDocumentationLocation);
		this.argument.setCriterion(keCriterion);
		assertEquals(this.argument.getCriterion().getId(), keCriterion.getId());
		assertEquals(this.argument.getCriterion().getSummary(), keCriterion.getSummary());
		assertEquals(this.argument.getCriterion().getDocumentationLocation(), keCriterion.getDocumentationLocationAsString());
	}
}
