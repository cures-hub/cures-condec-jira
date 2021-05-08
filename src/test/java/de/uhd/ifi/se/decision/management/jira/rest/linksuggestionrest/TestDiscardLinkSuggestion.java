package de.uhd.ifi.se.decision.management.jira.rest.linksuggestionrest;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDiscardLinkSuggestion extends TestConsistencyRestSuper {

	@Test
	public void testWithValidIssues() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		int actualStatus =
			super.consistencyRest
				.discardLinkSuggestion(request, project.getKey(), knowledgeElement0.getId(), knowledgeElement0.getDocumentationLocationAsString(),
					knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString()).getStatus();
		assertEquals("The response status should be OK (200).", 200, actualStatus);
	}

	@Test
	public void testWithInvalidIssues() {
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		int actualStatus =
			super.consistencyRest
				.discardLinkSuggestion(request, project.getKey(), null, null, knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString())
				.getStatus();
		assertEquals("The response status should be not OK.", 400, actualStatus);
	}


}
