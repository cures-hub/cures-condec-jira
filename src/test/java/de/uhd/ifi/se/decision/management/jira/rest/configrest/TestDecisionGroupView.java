package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionGroupView extends TestSetUp {

	private ConfigRest configRest;
	private KnowledgeElement newElement;

	@Before
	public void setUp() {
		configRest = new ConfigRest();
		init();
		configRest = new ConfigRest();
		long id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String projectKey = "TEST";
		String key = "Test";

		KnowledgeElement decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey,
				key, DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
		KnowledgePersistenceManager kpManager = new KnowledgePersistenceManager(projectKey);
		KnowledgeElement nextElement = kpManager
				.getManagerForSingleLocation(decisionKnowledgeElement.getDocumentationLocation())
				.insertKnowledgeElement(decisionKnowledgeElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupManager.insertGroup("TestGroup1", nextElement);

		KnowledgeElement element = new ChangedFile();
		element.setSummary("AbstractTestHandler.java");
		element.setDescription("TEST-3;");
		element.setProject("TEST");
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("TEST");
		newElement = ccManager.insertKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupManager.insertGroup("TestGroup2", newElement);

		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionElementsWithCertainGroup() {
		Response resp = configRest.getAllDecisionElementsWithCertainGroup("TEST", "TestGroup1");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("[TEST-1]", resp.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testGetAllClassElementsWithCertainGroup() {
		Response resp = configRest.getAllClassElementsWithCertainGroup("TEST", "TestGroup2");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
	}

	@Test
	@NonTransactional
	public void testRenameDecisionGroup() {
		Response resp = configRest.renameDecisionGroup("TEST", "TestGroup2", "NewTestGroup2");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("NewTestGroup2", DecisionGroupManager.getGroupsForElement(newElement).get(0));
		configRest.renameDecisionGroup("TEST", "NewTestGroup2", "TestGroup2");
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionGroup() {
		DecisionGroupManager.insertGroup("TestGroup3", newElement);
		configRest.deleteDecisionGroup("TEST", "TestGroup3");
		assertFalse(DecisionGroupManager.getGroupsForElement(newElement).contains("TestGroup3"));
	}

}
