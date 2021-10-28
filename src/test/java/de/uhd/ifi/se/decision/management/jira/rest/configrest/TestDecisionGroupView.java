package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionGroupView extends TestSetUp {

	private ConfigRest configRest;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		element = KnowledgeElements.getSolvedDecisionProblem();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", KnowledgeElements.getDecision());
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", element);

		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionElementsWithCertainGroup() {
		Response response = configRest.getAllDecisionElementsWithCertainGroup("TEST", "TestGroup1");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("[TEST-4]", response.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testGetAllClassElementsWithCertainGroup() {
		assertEquals(Response.Status.OK.getStatusCode(),
				configRest.getAllClassElementsWithCertainGroup("TEST", "TestGroup2").getStatus());
	}

	@Test
	@NonTransactional
	public void testRenameDecisionGroup() {
		Response resp = configRest.renameDecisionGroup("TEST", "TestGroup2", "NewTestGroup2");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertEquals("NewTestGroup2", DecisionGroupPersistenceManager.getGroupsForElement(element).get(0));
		configRest.renameDecisionGroup("TEST", "NewTestGroup2", "TestGroup2");
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionGroup() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup3", element);
		configRest.deleteDecisionGroup("TEST", "TestGroup3");
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup3"));
	}
}