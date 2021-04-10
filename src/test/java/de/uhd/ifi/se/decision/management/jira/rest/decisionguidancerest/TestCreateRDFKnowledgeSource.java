package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestCreateRDFKnowledgeSource extends TestSetUp {
	protected HttpServletRequest request;
	protected DecisionGuidanceRest decisionGuidanceRest;

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testCreateRDFKnowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource("NAME", "SERVICE", "QUERY", 30000, "");
		assertEquals(200, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", rdfSource).getStatus());
	}

	@Test
	public void testCreateRDFKnowledgeSourceInvalidProjectKey() {
		RDFSource rdfSource = new RDFSource("NAME2", "SERVICE", "QUERY", 30000, "");
		assertEquals(400, decisionGuidanceRest.createRDFKnowledgeSource(request, null, rdfSource).getStatus());
	}

	@Test
	public void testCreateRDFKnowledgeSourceInvalidTimeout() {
		RDFSource rdfSource = new RDFSource("NAME2", "SERVICE", "QUERY", -1, "");
		assertEquals(400, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", rdfSource).getStatus());
	}

	@Test
	public void testCreateRDFKnowledgeSourceAlreadyExisting() {
		RDFSource rdfSource = new RDFSource("NAME2", "SERVICE", "QUERY", 30000, "");
		assertEquals(200, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", rdfSource).getStatus());
		assertEquals(400, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", rdfSource).getStatus());
	}

	@Test
	public void testCreateRDFKnowledgeSourceRDFSourceNull() {
		assertEquals(400, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", null).getStatus());
	}

	@Test
	public void testCreateRDFKnowledgeSourcBlankName() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, "");
		assertEquals(400, decisionGuidanceRest.createRDFKnowledgeSource(request, "TEST", rdfSource).getStatus());
	}
}