package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUpdateKnowledgeSource extends TestSetUp {
	protected HttpServletRequest request;
	protected DecisionGuidanceRest decisionGuidanceRest;

	protected static String INVALID_PROJECT_KEY = "";
	protected static String VALID_PROJECT_KEY = "TEST";

	protected static String VALID_VALUE = "valid_value";
	protected static String INVALID_VALUE = "";

	@Before
	public void setUp() {
		init();
		decisionGuidanceRest = new DecisionGuidanceRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void tesUpdateRDFKnowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource("VALID SOURCE", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(200, decisionGuidanceRest
				.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURC", rdfSource).getStatus());
	}

	@Test
	public void testUpdateRDFKnowledgeSourceProjectKeyInvalid() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400, decisionGuidanceRest
				.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURCE", rdfSource).getStatus());
	}

	@Test
	public void testUpdateRDFKnowledgeSourceInValidRequest() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400, decisionGuidanceRest.updateKnowledgeSource(null, VALID_PROJECT_KEY, "VALID SOURCE", rdfSource)
				.getStatus());
	}
}