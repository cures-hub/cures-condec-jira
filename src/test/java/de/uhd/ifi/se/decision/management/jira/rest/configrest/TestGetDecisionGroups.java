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

public class TestGetDecisionGroups extends TestSetUp {

	protected ConfigRest configRest;
	private long id;
	private String projectKey;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		this.id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";
		String key = "Test";

		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey,
				key, DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
		DecisionGroupManager.insertGroup("TestGroup1", decisionKnowledgeElement);
	}

	@Test
	public void testGetDecisionGroupsIdInvalid() {
		Response response = configRest.getDecisionGroups(-1, DocumentationLocation.JIRAISSUE.getIdentifier(),
				this.projectKey);
		assertEquals("[]", response.getEntity().toString());
	}

	@Test
	public void testGetDecisionGroupsDocLocNull() {
		Response response = configRest.getDecisionGroups(this.id, null, this.projectKey);
		assertEquals("[]", response.getEntity().toString());
	}

	@Test
	public void testGetDecisionGroupsProjectKeyNull() {
		Response response = configRest.getDecisionGroups(this.id, DocumentationLocation.JIRAISSUE.getIdentifier(),
				null);
		assertEquals("[]", response.getEntity().toString());
	}

	@Test
	public void testGetDecisionGroupsNothingFound() {
		Response response = configRest.getDecisionGroups(this.id, DocumentationLocation.JIRAISSUE.getIdentifier(),
				"Test1");
		assertEquals("[]", response.getEntity().toString());
	}
	/*
	 * @Test public void testProjectKeyValid() { Response response =
	 * configRest.getDecisionGroups(this.id,
	 * DocumentationLocation.JIRAISSUE.getIdentifier(), this.projectKey);
	 * assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	 * assertEquals("[TestGroup1]", response.getEntity().toString()); }
	 */
}
