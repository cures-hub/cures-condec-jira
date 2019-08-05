package de.uhd.ifi.se.decision.management.jira.model;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDecisionKnowledgeElementTypeString extends TestSetUp {
	private long id;
	private String summary;
	private String description;
	private String projectKey;
	private DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		this.id = 100;
		this.summary = "Test";
		this.description = "Test";
		this.projectKey = "Test";
		String key = "Test";

		this.decisionKnowledgeElement = new DecisionKnowledgeElementImpl(id, summary, description, "Work-Item", projectKey,
				key, DocumentationLocation.ACTIVEOBJECT);
	}

	@Test
	public void testGetType(){
		assertEquals(KnowledgeType.OTHER, decisionKnowledgeElement.getType());
	}

	@Test
	public void testSetType(){
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl(id, summary, description, (String) null, projectKey,
				"Test", DocumentationLocation.ACTIVEOBJECT);
		assertEquals(KnowledgeType.OTHER, element.getType());
	}

	@Test
	public void testGetDocumentationLocationAsStringNull(){
		this.decisionKnowledgeElement.setDocumentationLocation((DocumentationLocation)null);
		assertEquals("", decisionKnowledgeElement.getDocumentationLocationAsString());
	}
}
