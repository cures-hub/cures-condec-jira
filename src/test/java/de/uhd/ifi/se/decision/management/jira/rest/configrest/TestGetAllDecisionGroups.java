package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetAllDecisionGroups extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", KnowledgeElements.getDecision());
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionGroups() {
		Response response = configRest.getAllDecisionGroups("TEST");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}