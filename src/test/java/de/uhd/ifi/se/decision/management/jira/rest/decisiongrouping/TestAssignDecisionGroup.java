package de.uhd.ifi.se.decision.management.jira.rest.decisiongrouping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGroupingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAssignDecisionGroup extends TestSetUp {

	private DecisionGroupingRest decisionGroupingRest;
	private KnowledgeElement decisionKnowledgeElementIss;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		decisionGroupingRest = new DecisionGroupingRest();
		init();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElementIss = new KnowledgeElement(issue);
		decisionKnowledgeElementIss.setType(KnowledgeType.ISSUE);
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testAssignDecisionGroupAddGroupEmpty() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "Safety", "", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 2);
	}

	@Test
	@NonTransactional
	public void testAssignDecisionGroupCurrentGroupEmpty() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "", "Safety", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 2);
	}

	@Test
	@NonTransactional
	public void testAssignDecisionGroupNoEmpties() {
		Response resp = decisionGroupingRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "Property,TestGroup",
				"Safety", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 4);
	}
}
