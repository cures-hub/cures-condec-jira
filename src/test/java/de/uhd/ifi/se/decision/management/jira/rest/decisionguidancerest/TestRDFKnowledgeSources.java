package de.uhd.ifi.se.decision.management.jira.rest.decisionguidancerest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.DecisionGuidanceRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestRDFKnowledgeSources extends TestSetUp {
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
	public void testSetRDFKnowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource("NAME", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(200,
				decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, rdfSource).getStatus());
	}

	@Test
	public void testSetRDFKnowledgeSourceInvalidProjectKey() {
		RDFSource rdfSource = new RDFSource("NAME2", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400,
				decisionGuidanceRest.setRDFKnowledgeSource(request, INVALID_PROJECT_KEY, rdfSource).getStatus());
	}

	public void testSetRDFKnowledgeSourceRDFSourceNull() {
		assertEquals(400, decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, null).getStatus());
	}

	@Test
	public void testSetRDFKnowledgeSourcBlankName() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400,
				decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, rdfSource).getStatus());
	}

	/**
	 * Test Set RDF Knowledge SOurce Activation
	 */

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with valid project and valid ")
	public void testActivateRDFKnowledgeSourceValidProjectValidValue() {
		assertEquals(200, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with invalid project and valid ")
	public void testActivateRDFKnowledgeSourceInValidProjectValidValue() {
		assertEquals(400, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with invalid project and INvalid ")
	public void testActivateRDFKnowledgeSourceInValidProjectInValidValue() {
		assertEquals(400, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}

	/**
	 * Test set Project Source
	 */
	@Test
	@DisplayName("Tests the method setProjectSource with valid project and valid value ")
	public void testSetProjectSourceValidProjectValidValue() {
		assertEquals(200, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with valid project and invalid value ")
	public void testSetProjectSourceValidProjectInValidValue() {
		assertEquals(400, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with invalid project and valid value ")
	public void testSetProjectSourceInValidProjectValidValue() {
		assertEquals(400, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with invalid project and invalid value ")
	public void testSetProjectSourceInValidProjectInValidValue() {
		assertEquals(400, decisionGuidanceRest
				.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}
}
