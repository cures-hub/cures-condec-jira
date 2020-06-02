package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestChecks extends TestConsistencyRestSuper  {


	@Test
	public void testDoesIssueNeedApproval() {
		Response response = consistencyRest.doesIssueNeedApproval(request, issues.get(0).getKey());
		assertEquals("Response should be OK (200).", 200, response.getStatus());
		response = consistencyRest.doesIssueNeedApproval(request, "InvalidKey");
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.doesIssueNeedApproval(request, null);
		assertEquals("Response should be 500.", 500, response.getStatus());

	}


	@Test
	public void testApproveIssue() {
		Response response = consistencyRest.approveCheck(request, issues.get(0).getKey(), "User");
		assertEquals("Response should be 400, because the check was not yet added.", 400, response.getStatus());
		ConsistencyCheckLogHelper.addCheck(issues.get(0));
		response = consistencyRest.approveCheck(request, issues.get(0).getKey(), "sysadmin");
		assertEquals("Response should be OK (200).", 200, response.getStatus());

		response = consistencyRest.approveCheck(request, "InvalidKey", "sysadmin");
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.approveCheck(request, "InvalidKey", null);
		assertEquals("Response should be 400.", 400, response.getStatus());
		response = consistencyRest.approveCheck(request, null, "sysadmin");
		assertEquals("Response should be 500.", 500, response.getStatus());
		response = consistencyRest.approveCheck(request, null, null);
		assertEquals("Response should be 500.", 500, response.getStatus());
	}

	@AfterEach
	public void reset(){
		ConsistencyCheckLogHelper.resetConsistencyCheckLogs();
	}

}
