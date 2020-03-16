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
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ConfigRestImpl;

public class TestGetAllDecisionGroups extends TestSetUp {

    protected ConfigRest configRest;
    private long id;
    private String summary;
    private String description;
    private KnowledgeType type;
    private String projectKey;
    private KnowledgeElement decisionKnowledgeElement;

    @Before
    public void setUp() {
	init();
	configRest = new ConfigRestImpl();
	this.id = 100;
	this.summary = "Test";
	this.description = "Test";
	this.type = KnowledgeType.SOLUTION;
	this.projectKey = "Test";
	String key = "Test";

	this.decisionKnowledgeElement = new KnowledgeElementImpl(id, summary, description, type, projectKey, key,
		DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
	DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
    }

    @Test
    public void testGetAllDecisionGroups() {
	Response response = configRest.getAllDecisionGroups(this.projectKey);
	assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	assertEquals("[TestGroup1]", response.getEntity().toString());
    }

}
