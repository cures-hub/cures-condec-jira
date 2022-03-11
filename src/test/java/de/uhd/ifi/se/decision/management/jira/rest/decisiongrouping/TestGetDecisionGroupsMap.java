package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGroupingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionGroupsMap extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;

	@Before
	public void setUp() {
		init();
		decisionGroupingRest = new DecisionGroupingRest();
		DecisionGroupPersistenceManager.insertGroup("UI", KnowledgeElements.getDecision());
	}

	@Test
	@NonTransactional
	public void testFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), decisionGroupingRest.getDecisionGroupsMap(null).getStatus());
	}

	@Test
	@NonTransactional
	public void testFilterSettingsValid() {
		assertEquals(Status.OK.getStatusCode(),
				decisionGroupingRest.getDecisionGroupsMap(new FilterSettings("TEST", "")).getStatus());
	}

}