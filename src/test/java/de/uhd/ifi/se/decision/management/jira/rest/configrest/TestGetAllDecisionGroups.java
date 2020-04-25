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
import de.uhd.ifi.se.decision.management.jira.rest.impl.ConfigRestImpl;

public class TestGetAllDecisionGroups extends TestSetUp {

    protected ConfigRest configRest;
    private String projectKey;

    @Before
    public void setUp() {
	init();
	configRest = new ConfigRestImpl();
	long id = 100;
	String summary = "Test";
	String description = "Test";
	KnowledgeType type = KnowledgeType.SOLUTION;
	this.projectKey = "Test";
	String key = "Test";

	KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey,
		key, DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
	DecisionGroupManager.insertGroup("TestGroup1", decisionKnowledgeElement);
    }

    @Test
    public void testGetAllDecisionGroups() {
	Response response = configRest.getAllDecisionGroups(this.projectKey);
	assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	assertEquals("[TestGroup1]", response.getEntity().toString());
    }

}
