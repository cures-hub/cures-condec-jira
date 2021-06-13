package de.uhd.ifi.se.decision.management.jira.rest.definitionofdonecheckingrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.rest.DefinitionOfDoneCheckingRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestChecks extends TestSetUp {

	protected HttpServletRequest request;
	protected DefinitionOfDoneCheckingRest dodCheckingRest;

	@Before
	public void setUp() {
		init();
		dodCheckingRest = new DefinitionOfDoneCheckingRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testGetFailedDefinitionOfDoneCriteria() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedDefinitionOfDoneCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedDefinitionOfDoneCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedDefinitionOfDoneCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteria() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaIssue() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.ISSUE);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaDecision() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.DECISION);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaAlternative() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.ALTERNATIVE);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaArgument() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.ARGUMENT);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaPro() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.PRO);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaCon() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.CON);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}

	@Test
	@NonTransactional
	public void testGetFailedCompletenessCheckCriteriaCode() {
		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		knowledgeElement.setType(KnowledgeType.CODE);
		FilterSettings settings = new FilterSettings("TEST", "");
		settings.setSelectedElement(knowledgeElement);
		Response response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(200, response.getStatus());

		settings.setSelectedElement((KnowledgeElement) null);
		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, settings);
		assertEquals(400, response.getStatus());

		response = dodCheckingRest.getFailedCompletenessCheckCriteria(request, null);
		assertEquals(400, response.getStatus());
	}
}
