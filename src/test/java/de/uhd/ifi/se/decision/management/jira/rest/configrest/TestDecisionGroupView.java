package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.CodeClassKnowledgeElementPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.KnowledgePersistenceManagerImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.rest.impl.ConfigRestImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestDecisionGroupView extends TestSetUp {

	private ConfigRest configRest;
	private long id;
	private String projectKey;
	KnowledgeElement newElement;

	private HttpServletRequest request;

	@Before
	public void setUp() {
		configRest = new ConfigRestImpl();
		init();
		configRest = new ConfigRestImpl();
		this.id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";
		String key = "Test";

		KnowledgeElement decisionKnowledgeElement = new KnowledgeElementImpl(id, summary, description, type, projectKey,
				key, DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);
		KnowledgePersistenceManager kpManager = new KnowledgePersistenceManagerImpl(projectKey);
		KnowledgeElement nextElement =
				kpManager.getManagerForSingleLocation(decisionKnowledgeElement.getDocumentationLocation())
						.insertDecisionKnowledgeElement(decisionKnowledgeElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupManager.insertGroup("TestGroup1", nextElement);

		KnowledgeElement element = new KnowledgeElementImpl();
		element.setDocumentationLocation(DocumentationLocation.COMMIT);
		element.setSummary("AbstractTestHandler.java");
		element.setDescription("TEST-3;");
		element.setProject("TEST");
		element.setType(KnowledgeType.OTHER);
		CodeClassKnowledgeElementPersistenceManager ccManager = new CodeClassKnowledgeElementPersistenceManager("TEST");
		newElement = ccManager.insertDecisionKnowledgeElement(element, JiraUsers.SYS_ADMIN.getApplicationUser());
		DecisionGroupManager.insertGroup("TestGroup2", newElement);

		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testGetAllDecisionElementsWithCertainGroup() {
		Response resp = configRest.getAllDecisionElementsWithCertainGroup("TEST", "TestGroup1");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("[TEST-1]", resp.getEntity().toString());
	}

	@Test
	public void testGetAllClassElementsWithCertainGroup() {
		Response resp = configRest.getAllClassElementsWithCertainGroup("TEST", "TestGroup2");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("[TEST-1]", resp.getEntity().toString());
	}

	@Test
	public void testRenameDecisionGroup() {
		Response resp = configRest.renameDecisionGroup("TEST", "TestGroup2", "NewTestGroup2");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("NewTestGroup2", DecisionGroupManager.getGroupsForElement(newElement).get(0));
		configRest.renameDecisionGroup("TEST", "NewTestGroup2", "TestGroup2");
	}

	@Test
	public void testDeleteDecisionGroup() {
		DecisionGroupManager.insertGroup("TestGroup3", newElement);
		Response resp = configRest.deleteDecisionGroup("TEST", "TestGroup3");
		assertFalse(DecisionGroupManager.getGroupsForElement(newElement).contains("TestGroup3"));
	}


}
