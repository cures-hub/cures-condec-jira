package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGroupingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteDecisionGroup extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;

	@Before
	public void setUp() {
		decisionGroupingRest = new DecisionGroupingRest();
		init();
		KnowledgeElements.getSolvedDecisionProblem();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", KnowledgeElements.getDecision());
	}

	@Test
	@NonTransactional
	public void testGroupNameValidProjectKeyInvalid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGroupingRest.deleteDecisionGroup(null, "TestGroup").getStatus());
	}

	@Test
	@NonTransactional
	public void testGroupNameValidProjectKeyValid() {
		assertEquals(Response.Status.OK.getStatusCode(),
				decisionGroupingRest.deleteDecisionGroup("TEST", "TestGroup").getStatus());
	}

	@Test
	@NonTransactional
	public void testGroupNameInvalidProjectKeyValid() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGroupingRest.deleteDecisionGroup("TEST", "TestGroupNotExisting").getStatus());
	}
}