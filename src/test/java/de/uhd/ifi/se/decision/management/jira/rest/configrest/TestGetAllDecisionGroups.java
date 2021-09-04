package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

public class TestGetAllDecisionGroups extends TestSetUp {

	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		long id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String key = "Test";

		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, "TEST", key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
		DecisionGroupManager.insertGroup("TestGroup1", decisionKnowledgeElement);
	}

	@Test
	public void testGetAllDecisionGroups() {
		Response response = configRest.getAllDecisionGroups("TEST");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

}
