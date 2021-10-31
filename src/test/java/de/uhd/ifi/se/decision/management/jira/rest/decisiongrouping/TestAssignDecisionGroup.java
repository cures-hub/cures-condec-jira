package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

public class TestAssignDecisionGroup extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;
	private KnowledgeElement element;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		decisionGroupingRest = new DecisionGroupingRest();
		init();
		element = KnowledgeElements.getSolvedDecisionProblem();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testAddGroupEmpty() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, "High_Level", "Safety", "", element);
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("Safety"));
		assertEquals(2, DecisionGroupPersistenceManager.getGroupsForElement(element).size());
	}

	@Test
	@NonTransactional
	public void testCurrentGroupsEmpty() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, "High_Level", "", "Safety", element);
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("Safety"));
		assertEquals(2, DecisionGroupPersistenceManager.getGroupsForElement(element).size());
	}

	@Test
	@NonTransactional
	public void testGroupNoEmpties() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, "High_Level", "Property,TestGroup", "Safety",
				element);
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("Safety"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).size() == 4);
	}

	@Test
	@NonTransactional
	public void testElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				decisionGroupingRest.assignDecisionGroup(request, "High_Level", "Security", "", null).getStatus());
	}
}
