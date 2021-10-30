package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGroupingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionGroupView extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		decisionGroupingRest = new DecisionGroupingRest();
		element = KnowledgeElements.getSolvedDecisionProblem();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", KnowledgeElements.getDecision());
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", element);

		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testGetAllDecisionElementsWithCertainGroup() {
		Response response = decisionGroupingRest.getAllDecisionElementsWithCertainGroup("TEST", "TestGroup1");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals("[TEST-4]", response.getEntity().toString());
	}

	@Test
	@NonTransactional
	public void testGetAllClassElementsWithCertainGroup() {
		assertEquals(Response.Status.OK.getStatusCode(),
				decisionGroupingRest.getAllClassElementsWithCertainGroup("TEST", "TestGroup2").getStatus());
	}
}