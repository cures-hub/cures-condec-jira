package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestUpdateKnowledgeSource extends TestSetUp {
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
	public void testUpdateRDFKnowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource("VALID SOURCE", "SERVICE", "QUERY", 30000, "");
		assertEquals(200, decisionGuidanceRest.updateRDFKnowledgeSource(request, "TEST", "DBPedia - Frameworks", rdfSource)
				.getStatus());
	}

	@Test
	public void testUpdateRDFKnowledgeSourceProjectKeyInvalid() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, "");
		assertEquals(400, decisionGuidanceRest.updateRDFKnowledgeSource(request, "TEST", "DBPedia - Frameworks", rdfSource)
				.getStatus());
	}

	@Test
	public void testUpdateRDFKnowledgeSourceInValidRequest() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, "");
		assertEquals(400, decisionGuidanceRest.updateRDFKnowledgeSource(null, "TEST", "DBPedia - Frameworks", rdfSource)
				.getStatus());
	}
}