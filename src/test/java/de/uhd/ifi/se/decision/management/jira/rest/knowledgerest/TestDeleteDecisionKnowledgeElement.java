package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDeleteDecisionKnowledgeElement extends TestSetUpWithIssues {

	private final static String DELETION_ERROR = "Deletion of decision knowledge element failed.";

	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;
	private DecisionKnowledgeElement decisionKnowledgeElement;
	private HttpServletRequest request;

	@Before
	public void setUp() {
		knowledgeRest = new KnowledgeRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setType(KnowledgeType.SOLUTION);

		request = new MockHttpServletRequest();
		request.setAttribute("WithFails", false);
		request.setAttribute("NoFails", true);
	}

	@Test
	public void testRequestFilledElementFilled() {
		assertEquals(Response.Status.OK.getStatusCode(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getStatus());
	}

	@Test
	public void testRequestErrorElementFilled() {
		HttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute("WithFails", true);
		request.setAttribute("NoFails", false);
		assertEquals(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", DELETION_ERROR))
						.build().getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(request, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestNullElementNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteDecisionKnowledgeElement(null, null).getEntity());
	}

	@Test
	public void testRequestNullElementFilled() {
		assertEquals(
				Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR)).build()
						.getEntity(),
				knowledgeRest.deleteDecisionKnowledgeElement(null, decisionKnowledgeElement).getEntity());
	}

	@Test
	public void testRequestFilledElementNull() {
		assertEquals(Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error", DELETION_ERROR))
				.build().getEntity(), knowledgeRest.deleteDecisionKnowledgeElement(request, null).getEntity());
	}
}
