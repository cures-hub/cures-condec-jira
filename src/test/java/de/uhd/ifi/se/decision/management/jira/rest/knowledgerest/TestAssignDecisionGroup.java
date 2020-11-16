package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestAssignDecisionGroup extends TestSetUp {

	private KnowledgeRest knowledgeRest;
	private KnowledgeElement decisionKnowledgeElementIss;
	// private KnowledgeElement decisionKnowledgeElemenDec;
	// private KnowledgeElement decisionKnowledgeElementAlt;
	// private KnowledgeElement decisionKnowledgeElementPro;
	// private KnowledgeElement decisionKnowledgeElementCon;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		init();

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3");
		decisionKnowledgeElementIss = new KnowledgeElement(issue);
		decisionKnowledgeElementIss.setType(KnowledgeType.ISSUE);
		/*
		 * decisionKnowledgeElemenDec = new KnowledgeElement(issue);
		 * decisionKnowledgeElemenDec.setType(KnowledgeType.DECISION);
		 * decisionKnowledgeElementAlt = new KnowledgeElement(issue);
		 * decisionKnowledgeElementAlt.setType(KnowledgeType.ALTERNATIVE);
		 * decisionKnowledgeElementPro = new KnowledgeElement(issue);
		 * decisionKnowledgeElementPro.setType(KnowledgeType.PRO);
		 * decisionKnowledgeElementCon = new KnowledgeElement(issue);
		 * decisionKnowledgeElementCon.setType(KnowledgeType.CON);
		 */

		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testAssignDecisionGroupAddGroupEmpty() {
		Response resp = knowledgeRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "Safety", "", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 2);
	}

	@Test
	public void testAssignDecisionGroupCurrentGroupEmpty() {
		Response resp = knowledgeRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "", "Safety", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 2);
	}

	@Test
	public void testAssignDecisionGroupNoEmpties() {
		Response resp = knowledgeRest.assignDecisionGroup(request, decisionKnowledgeElementIss.getId(),
				decisionKnowledgeElementIss.getDocumentationLocationAsString(), "High_Level", "Property,TestGroup",
				"Safety", "TEST");
		assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).contains("Safety"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElementIss).size() == 4);
	}

	@After
	public void tearDown() {
		KnowledgeGraph.instances.clear();
	}
}
