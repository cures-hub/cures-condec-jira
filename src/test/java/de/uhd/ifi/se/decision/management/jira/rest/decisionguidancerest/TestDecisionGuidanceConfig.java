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

public class TestDecisionGuidanceConfig extends TestSetUp {
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

	/**
	 * Test Set RDF Knowledge Source
	 */
	@Test
	@DisplayName("Tests the method setRDFSource with valid value.")
	public void testSetRDFKnowledgeSource() {
		RDFSource rdfSource = new RDFSource("NAME", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(200,
				decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, rdfSource).getStatus());
	}

	@Test
	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourceInvalidProject() {
		RDFSource rdfSource = new RDFSource("NAME2", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400,
				decisionGuidanceRest.setRDFKnowledgeSource(request, INVALID_PROJECT_KEY, rdfSource).getStatus());
	}

	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourceRDFSourceNull() {
		assertEquals(400, decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, null).getStatus());
	}

	@Test
	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourcBlankName() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400,
				decisionGuidanceRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, rdfSource).getStatus());
	}

	/**
	 * Test Delete RDF Knowledge Source
	 */

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with valid value.")
	public void testDeleteRDFKnowledgeSourceValid() {
		assertEquals(200,
				decisionGuidanceRest.deleteKnowledgeSource(request, VALID_PROJECT_KEY, VALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with invalid value and valid project.")
	public void testDeleteRDFKnowledgeSourceInvalidValue() {
		assertEquals(400,
				decisionGuidanceRest.deleteKnowledgeSource(request, VALID_PROJECT_KEY, INVALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with valid value and invalid project")
	public void testDeleteRDFKnowledgeSourceInvalidProject() {
		assertEquals(400,
				decisionGuidanceRest.deleteKnowledgeSource(request, INVALID_PROJECT_KEY, VALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with invalid value and invalid Project.")
	public void testDeleteRDFKnowledgeSourceInvalidProjectInvalidValue() {
		assertEquals(400,
				decisionGuidanceRest.deleteKnowledgeSource(request, INVALID_PROJECT_KEY, INVALID_VALUE).getStatus());
	}

	/**
	 * Test Update RDF Knowledge Source
	 */

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void tesUpdateRDFKnowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource("VALID SOURCE", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(200, decisionGuidanceRest
				.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURC", rdfSource).getStatus());
	}

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void testUpdateRDFKnowledgeSourceInValid() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400, decisionGuidanceRest
				.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURCE", rdfSource).getStatus());
	}

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void testUpdateRDFKnowledgeSourceInValidRequest() {
		RDFSource rdfSource = new RDFSource("", "SERVICE", "QUERY", 30000, 10, "");
		assertEquals(400, decisionGuidanceRest.updateKnowledgeSource(null, VALID_PROJECT_KEY, "VALID SOURCE", rdfSource)
				.getStatus());
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

	@Test
	@DisplayName("Tests the method setMaxRecommendations valid Value")
	public void testSetMaxRecommendationsValid() {
		assertEquals(200, decisionGuidanceRest.setMaxNumberRecommendations(request, VALID_PROJECT_KEY, 20).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMaxRecommendations invalid Value")
	public void testSetMaxRecommendationsInvalidValue() {
		assertEquals(400,
				decisionGuidanceRest.setMaxNumberRecommendations(request, VALID_PROJECT_KEY, -20).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMaxRecommendations invalid project")
	public void testSetMaxRecommendationsInvalidProject() {
		assertEquals(400,
				decisionGuidanceRest.setMaxNumberRecommendations(request, INVALID_PROJECT_KEY, 20).getStatus());
	}

	@Test
	@DisplayName("Tests the method addRecommendationdirectly valid project")
	public void testSetAddRecommendationDirectlyValidProject() {
		assertEquals(200,
				decisionGuidanceRest.setAddRecommendationDirectly(request, VALID_PROJECT_KEY, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method addRecommendationdirectly invalid project")
	public void testSetAddRecommendationDirectlyInValidProject() {
		assertEquals(400,
				decisionGuidanceRest.setAddRecommendationDirectly(request, INVALID_PROJECT_KEY, true).getStatus());
	}

	@Test
	public void testSetRecommendationInput() {
		assertEquals(200,
				decisionGuidanceRest.setRecommendationInput(request, VALID_PROJECT_KEY, "KEYWORD", true).getStatus());
		assertEquals(400,
				decisionGuidanceRest.setRecommendationInput(null, VALID_PROJECT_KEY, "KEYWORD", true).getStatus());
		assertEquals(400,
				decisionGuidanceRest.setRecommendationInput(request, INVALID_PROJECT_KEY, "KEYWORD", true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setSimilarityThreshold")
	public void testSetSimilarityThreshold() {
		assertEquals(200, decisionGuidanceRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, 1).getStatus());
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, INVALID_PROJECT_KEY, 1).getStatus());
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, -1).getStatus());
		assertEquals(400, decisionGuidanceRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, 3).getStatus());
	}

	@Test
	@DisplayName("Tests the method setIrrelevantWords")
	public void testSetIrrelevantWords() {
		assertEquals(200,
				decisionGuidanceRest.setIrrelevantWords(request, VALID_PROJECT_KEY, "WHICH;WHAT;SHOULD").getStatus());
		assertEquals(400, decisionGuidanceRest.setIrrelevantWords(request, VALID_PROJECT_KEY, "").getStatus());
	}

}
