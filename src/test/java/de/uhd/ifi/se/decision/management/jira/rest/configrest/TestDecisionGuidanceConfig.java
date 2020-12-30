package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

public class TestDecisionGuidanceConfig extends TestSetUp {
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	protected static String INVALID_PROJECT_KEY = "";
	protected static String VALID_PROJECT_KEY = "TEST";

	protected static String VALID_VALUE = "valid_value";
	protected static String INVALID_VALUE = "";

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	/**
	 * Test Set RDF Knowledge Source
	 */

	@Test
	@DisplayName("Tests the method setRDFSource with valid value.")
	public void testSetRDFKnowledgeSource() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "NAME", "30000", 10);
		Gson gson = new Gson();
		assertEquals(200,
			configRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, gson.toJson(rdfSource)).getStatus());
	}

	@Test
	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourceInvalidProject() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "NAME2", "30000", 10);
		Gson gson = new Gson();
		assertEquals(400,
			configRest.setRDFKnowledgeSource(request, INVALID_PROJECT_KEY, gson.toJson(rdfSource)).getStatus());
	}

	@Test(expected = JsonSyntaxException.class)
	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourceInvalidJSON() {
		assertEquals(400, configRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, "-123").getStatus());
	}

	@Test
	@DisplayName("Tests the method setRDFSource with invalid value.")
	public void testSetRDFKnowledgeSourcBlankName() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "", "30000", 10);
		Gson gson = new Gson();
		assertEquals(400,
			configRest.setRDFKnowledgeSource(request, VALID_PROJECT_KEY, gson.toJson(rdfSource)).getStatus());
	}

	/**
	 * Test Delete RDF Knowledge Source
	 */

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with valid value.")
	public void testDeleteRDFKNowledgeSourceValid() {
		assertEquals(200, configRest.deleteKnowledgeSource(request, VALID_PROJECT_KEY, VALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with invalid value and valid project.")
	public void testDeleteRDFKNowledgeSourceInvalidValue() {
		assertEquals(400, configRest.deleteKnowledgeSource(request, VALID_PROJECT_KEY, INVALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with valid value and invalid project")
	public void testDeleteRDFKNowledgeSourceInvalidProject() {
		assertEquals(400, configRest.deleteKnowledgeSource(request, INVALID_PROJECT_KEY, VALID_VALUE).getStatus());
	}

	@Test
	@DisplayName("Tests the method deleteRDFKnowledgeSource with invalid value and invalid Project.")
	public void testDeleteRDFKNowledgeSourceInvalidProjectInvalidValue() {
		assertEquals(400, configRest.deleteKnowledgeSource(request, INVALID_PROJECT_KEY, INVALID_VALUE).getStatus());
	}

	/**
	 * Test Update RDF Knowledge Source
	 */

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void tesUpdateRDFKNowledgeSourceValid() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "VALID SOURCE", "30000", 10);
		Gson gson = new Gson();
		assertEquals(200, configRest
			.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURC", gson.toJson(rdfSource)).getStatus());
	}

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void testUpdateRDFKNowledgeSourceInValid() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "", "30000", 10);
		Gson gson = new Gson();
		assertEquals(400, configRest
			.updateKnowledgeSource(request, VALID_PROJECT_KEY, "VALID SOURCE", gson.toJson(rdfSource)).getStatus());
	}

	@Test
	@DisplayName("Tests the method updateKnowledgeSource with valid value.")
	public void testUpdateRDFKNowledgeSourceInValidRequest() {
		RDFSource rdfSource = new RDFSource(VALID_PROJECT_KEY, "SERVICE", "QUERY", "", "30000", 10);
		Gson gson = new Gson();
		assertEquals(400, configRest
			.updateKnowledgeSource(null, VALID_PROJECT_KEY, "VALID SOURCE", gson.toJson(rdfSource)).getStatus());
	}

	/**
	 * Test Set RDF Knowledge SOurce Activation
	 */

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with valid project and valid ")
	public void testActivateRDFKNowledgeSourceValidProjectValidValue() {
		assertEquals(200,
			configRest.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with invalid project and valid ")
	public void testActivateRDFKNowledgeSourceInValidProjectValidValue() {
		assertEquals(400,
			configRest.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setKnowledgeSourceActivation with invalid project and INvalid ")
	public void testActivateRDFKNowledgeSourceInValidProjectInValidValue() {
		assertEquals(400,
			configRest.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}

	/**
	 * Test set Project Source
	 */
	@Test
	@DisplayName("Tests the method setProjectSource with valid project and valid value ")
	public void testSetProjectSourceValidProjectValidValue() {
		assertEquals(200,
			configRest.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with valid project and invalid value ")
	public void testSetProjectSourceValidProjectInValidValue() {
		assertEquals(400,
			configRest.setKnowledgeSourceActivated(request, VALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with invalid project and valid value ")
	public void testSetProjectSourceInValidProjectValidValue() {
		assertEquals(400,
			configRest.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, VALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setProjectSource with invalid project and invalid value ")
	public void testSetProjectSourceInValidProjectInValidValue() {
		assertEquals(400,
			configRest.setKnowledgeSourceActivated(request, INVALID_PROJECT_KEY, INVALID_VALUE, true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMaxRecommendations valid Value")
	public void testSetMaxRecommendationsValid() {
		assertEquals(200, configRest.setMaxNumberRecommendations(request, VALID_PROJECT_KEY, 20).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMaxRecommendations invalid Value")
	public void testSetMaxRecommendationsInvalidValue() {
		assertEquals(400, configRest.setMaxNumberRecommendations(request, VALID_PROJECT_KEY, -20).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMaxRecommendations invalid project")
	public void testSetMaxRecommendationsInvalidProject() {
		assertEquals(400, configRest.setMaxNumberRecommendations(request, INVALID_PROJECT_KEY, 20).getStatus());
	}

	@Test
	@DisplayName("Tests the method addRecommendationdirectly valid project")
	public void testSetAddRecommendationDirectlyValidProject() {
		assertEquals(200, configRest.setAddRecommendationDirectly(request, VALID_PROJECT_KEY, "true").getStatus());
	}

	@Test
	@DisplayName("Tests the method addRecommendationdirectly invalid project")
	public void testSetAddRecommendationDirectlyInValidProject() {
		assertEquals(400, configRest.setAddRecommendationDirectly(request, INVALID_PROJECT_KEY, "true").getStatus());
	}

	@Test
	@DisplayName("Tests the method addRecommendationdirectly invalid project")
	public void testSetAddRecommendationDirectlyInvalidValue() {
		assertEquals(200,
			configRest.setAddRecommendationDirectly(request, VALID_PROJECT_KEY, "invalidvalue").getStatus());
	}

	@Test
	public void testSetRecommendationInput() {
		assertEquals(200, configRest.setRecommendationInput(request, VALID_PROJECT_KEY, "KEYWORD", true).getStatus());
		assertEquals(400, configRest.setRecommendationInput(null, VALID_PROJECT_KEY, "KEYWORD", true).getStatus());
		assertEquals(400, configRest.setRecommendationInput(request, INVALID_PROJECT_KEY, "KEYWORD", true).getStatus());
	}

	@Test
	@DisplayName("Tests the method setSimilarityThreshold")
	public void testSetSimilarityThreshold() {
		assertEquals(200, configRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, 1).getStatus());
		assertEquals(400, configRest.setSimilarityThreshold(request, INVALID_PROJECT_KEY, 1).getStatus());
		assertEquals(400, configRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, -1).getStatus());
		assertEquals(400, configRest.setSimilarityThreshold(request, VALID_PROJECT_KEY, 3).getStatus());
	}

}
