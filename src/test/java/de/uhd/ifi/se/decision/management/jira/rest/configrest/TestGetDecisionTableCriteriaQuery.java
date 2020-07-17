package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class TestGetDecisionTableCriteriaQuery extends TestConfigSuper {

	private String testQuery = "?jql=project=Test";
	
	@Test
	public void getDecisionTableCriteriaQueryWithInvalidProjectKey() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), 
				configRest.getDesionTabelCriteriaQuery(null).getEntity());
	}

	@Test
	public void setDecisionTableCriteriaQueryWith() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(), 
				configRest.setDesionTabelCriteriaQuery(request, "TEST", testQuery).getStatus());
	}
	
	@Test
	public void getDecisionTableCriteriaQueryWithIvalidProjectKey() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(), 
				configRest.getDesionTabelCriteriaQuery("TEST").getStatus());
	}	
}
