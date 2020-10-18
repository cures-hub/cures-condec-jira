package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;

public class TestChecks extends TestConsistencyRestSuper {

	@Test
	public void testDoesIssueNeedApproval() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));
		Response response = consistencyRest.doesElementNeedApproval(request,
				knowledgeElement.getProject().getProjectKey(), knowledgeElement.getId(),
				knowledgeElement.getDocumentationLocationAsString());
		assertEquals("Response should be OK (200).", 200, response.getStatus());
		response = consistencyRest.doesElementNeedApproval(request, "InvalidKey", null, null);
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.doesElementNeedApproval(request, null, null, null);
		assertEquals("Response should be 500.", 400, response.getStatus());

	}

	@Test
	public void testDoesIssueNeedCompletenessApproval() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = consistencyRest.doesElementNeedCompletenessApproval(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = consistencyRest.doesElementNeedCompletenessApproval(request, settings);
		assertEquals(400, response.getStatus());

		response = consistencyRest.doesElementNeedCompletenessApproval(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	public void testApproveIssue() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(issues.get(0));

		Response response = consistencyRest.approveCheck(request, knowledgeElement.getProject().getProjectKey(),
				knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString(), "User");
		assertEquals("Response should be 400, because the check was not yet added.", 400, response.getStatus());
		ConsistencyCheckLogHelper.addCheck(knowledgeElement);

		response = consistencyRest.approveCheck(request, knowledgeElement.getProject().getProjectKey(),
				knowledgeElement.getId(), knowledgeElement.getDocumentationLocationAsString(), "sysadmin");
		assertEquals("Response should be OK (200).", 200, response.getStatus());

		response = consistencyRest.approveCheck(request, "InvalidKey", null, null, "sysadmin");
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.approveCheck(request, "InvalidKey", null, null, null);
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.approveCheck(request, null, null, null, "sysadmin");
		assertEquals("Response should be 500.", 400, response.getStatus());
		response = consistencyRest.approveCheck(request, null, null, null, null);
		assertEquals("Response should be 500.", 400, response.getStatus());
	}

	@AfterEach
	public void reset() {
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();
	}

}
