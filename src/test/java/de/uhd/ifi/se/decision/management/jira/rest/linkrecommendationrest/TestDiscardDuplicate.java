package de.uhd.ifi.se.decision.management.jira.rest.linkrecommendationrest;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDiscardDuplicate extends TestConsistencyRestSuper {

	@Test
	public void testDiscardDetectedDuplicate() {
		KnowledgeElement knowledgeElement0 = new KnowledgeElement(issues.get(0));
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(issues.get(1));
		assertEquals("Request should be valid.", 200, consistencyRest.discardDetectedDuplicate(request, project.getKey(), knowledgeElement0.getId(), knowledgeElement0.getDocumentationLocationAsString(),
			knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, project.getKey(), -1L, "", knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, project.getKey(), null, "", knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString()).getStatus());
		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, project.getKey(), knowledgeElement0.getId(), knowledgeElement0.getDocumentationLocationAsString(), null, null).getStatus());

		assertEquals("Request should be invalid.", 400, consistencyRest.discardDetectedDuplicate(request, null, knowledgeElement0.getId(), knowledgeElement0.getDocumentationLocationAsString(),
			knowledgeElement1.getId(), knowledgeElement1.getDocumentationLocationAsString()).getStatus());
	}

}
