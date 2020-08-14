package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TestGetDecisionTableCriteriaQuery extends TestConfigSuper {

	private String projectKey = "TEST";
	private String testQuery = "project=Test";

	@Test
	public void testGetDecisionTableCriteriaQueryWithInvalidProjectKey() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.getDesionTabelCriteriaQuery(null).getEntity());
	}

	@Test
	public void testSetDecisionTableCriteriaQuery() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.setDesionTabelCriteriaQuery(request, projectKey, testQuery).getStatus());
	}

	@Test
	public void testGetDecisionTableCriteriaQuery() {
		assertEquals(Response.status(Response.Status.OK).build().getStatus(),
				configRest.getDesionTabelCriteriaQuery(projectKey).getStatus());
	}

	@Test
	public void testTestDecisionTableCriteriaQueryWithInvalidProjectKey() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(),
				configRest.testDecisionTableCriteriaQuery(request, null, testQuery).getEntity());
	}
	@Ignore
	@Test
	public void testTestDecisionTableCriteriaQuery() {
		assertEquals(
				"{1=[WI: Implement feature], 2=[How can we implement the feature?], 3=[We could do it like this!], 4=[We will do it like this!], 5=[This is a great solution.], 6=[NFR: Usabililty], 12=[How can we implement the new get function?], 14=[WI: Yet another work item], 30=[WI: Do an interesting task]}",
				configRest.testDecisionTableCriteriaQuery(request, projectKey, testQuery).getEntity().toString());
	}
}
