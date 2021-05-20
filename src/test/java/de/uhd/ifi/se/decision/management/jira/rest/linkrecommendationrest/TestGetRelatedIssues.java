package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetRelatedIssues extends TestConsistencyRestSuper {

	@Test
	public void testWithValidIssue() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));
		int actualStatus = super.consistencyRest.getRelatedKnowledgeElements(request, knowledgeElement.getProject().getProjectKey(), knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString()).getStatus();
		assertEquals("The response status should be OK (200).", 200, actualStatus);
	}

	@Test
	public void testWithInvalidIssue() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));

		int actualStatus = super.consistencyRest.getRelatedKnowledgeElements(request, "GIBBERISH", knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString()).getStatus();
		assertEquals("The response status should be server error (400).", 400, actualStatus);
	}
}
