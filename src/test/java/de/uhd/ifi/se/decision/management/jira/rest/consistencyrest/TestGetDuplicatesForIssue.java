package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.DuplicateSuggestion;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetDuplicatesForIssue extends TestConsistencyRestSuper {

	@Test
	public void testGetDuplicatesForIssue() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));
		KnowledgePersistenceManager.getOrCreate(knowledgeElement.getProject().getProjectKey());
		Response response = consistencyRest.getDuplicateKnowledgeElements(request, knowledgeElement.getProject().getProjectKey(), knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Request should be valid.", 200, response.getStatus());
		String oldDescription = issues.get(1).getDescription();
		issues.get(1).setDescription(issues.get(0).getDescription());
		response = consistencyRest.getDuplicateKnowledgeElements(request, knowledgeElement.getProject().getProjectKey(), knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Request should be valid.", 200, response.getStatus());

		issues.get(1).setDescription(oldDescription);
		response = consistencyRest.getDuplicateKnowledgeElements(request, "null", knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Request should be invalid.", 400, response.getStatus());
		response = consistencyRest.getDuplicateKnowledgeElements(request, knowledgeElement.getProject().getProjectKey(), -1L, knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Request should be invalid.", 400, response.getStatus());
		response = consistencyRest.getDuplicateKnowledgeElements(request, null, knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Request should be invalid.", 400, response.getStatus());
	}

	@Test
	public void testDuplicatesToJsonMap() {
		DuplicateSuggestion testDuplicate = null;
		assertEquals("Request should be valid.", 0, consistencyRest.duplicateToJsonMap(testDuplicate).size());
		testDuplicate = new DuplicateSuggestion(new KnowledgeElement(issues.get(0)), new KnowledgeElement(issues.get(1)), issues.get(1).getSummary(), 0, 12, "description");
		assertEquals("Request should be valid.", issues.get(1).getSummary(), ((KnowledgeElement) consistencyRest.duplicateToJsonMap(testDuplicate).get("duplicateElement")).getSummary());

	}
}
