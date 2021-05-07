package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.rest.ConsistencyRest;
import de.uhd.ifi.se.decision.management.jira.rest.configrest.TestConfigSuper;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestConsistencyConfig extends TestConfigSuper {
	protected HttpServletRequest request;
	protected ConsistencyRest configRest;

	protected static String VALID_PROJECT_KEY = "TEST";

	@Before
	public void setUp() {
		init();
		configRest = new ConsistencyRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	// Tests for the method testSetMinimumLinkSuggestionProbability
	@Test
	@DisplayName("Tests the method testSetMinimumLinkSuggestionProbability with an invalid negative value.")
	public void testSetMinimumLinkSuggestionProbabilityWithNegativeValue() {
		assertEquals(400, configRest.setMinimumLinkSuggestionProbability(request, VALID_PROJECT_KEY, -1).getStatus());
	}

	@Test
	@DisplayName("Tests the method testSetMinimumLinkSuggestionProbability with an edge case value of zero.")
	public void testSetMinimumLinkSuggestionProbabilityWithEdgeCaseZero() {
		assertEquals(200, configRest.setMinimumLinkSuggestionProbability(request, VALID_PROJECT_KEY, 0).getStatus());
	}

	@Test
	@DisplayName("Tests the method testSetMinimumLinkSuggestionProbability with an edge case value of one.")
	public void testSetMinimumLinkSuggestionProbabilityWithEdgeCaseOne() {
		assertEquals(200, configRest.setMinimumLinkSuggestionProbability(request, VALID_PROJECT_KEY, 0.3).getStatus());
	}

	@Test
	@DisplayName("Tests the method testSetMinimumLinkSuggestionProbability with a valid value.")
	public void testSetMinimumLinkSuggestionProbabilityWithValidValue() {
		assertEquals(200, configRest.setMinimumLinkSuggestionProbability(request, VALID_PROJECT_KEY, 1).getStatus());

	}

	@Test
	@DisplayName("Tests the method testSetMinimumLinkSuggestionProbability with an invalid value greater than one.")
	public void testSetMinimumLinkSuggestionProbabilityWithValueGreaterOne() {
		assertEquals(400, configRest.setMinimumLinkSuggestionProbability(request, VALID_PROJECT_KEY, 2).getStatus());
	}

	// Tests for the method setMinimumDuplicateLength
	@Test
	@DisplayName("Tests the method setMinimumDuplicateLength with a invalid value smaller than three.")
	public void testSetMinimumDuplicateLengthWithInvalidValueSmallerThanThree() {
		assertEquals(400, configRest.setMinimumDuplicateLength(request, VALID_PROJECT_KEY, 0).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMinimumDuplicateLength with a valid edge case value of three.")
	public void testSetMinimumDuplicateLengthWithValidEdgeCaseValueThree() {
		assertEquals(200, configRest.setMinimumDuplicateLength(request, VALID_PROJECT_KEY, 3).getStatus());
	}

	@Test
	@DisplayName("Tests the method setMinimumDuplicateLength with a valid value greater than three.")
	public void testSetMinimumDuplicateLengthWithValidValue() {
		assertEquals(200, configRest.setMinimumDuplicateLength(request, VALID_PROJECT_KEY, 9).getStatus());
	}

	// Tests for the method activateQualityEvent
	@Test
	@DisplayName("Tests the method activateQualityEvent.")
	public void testActivateConsistencyEventWithValidData() {
		assertEquals(200, configRest.activateQualityEvent(request, VALID_PROJECT_KEY, "done", true).getStatus());
	}

	@Test
	@DisplayName("Tests the method activateQualityEvent.")
	public void testActivateConsistencyEventWithInvalidData() {
		assertEquals(400, configRest.activateQualityEvent(request, VALID_PROJECT_KEY, "none", true).getStatus());
	}
}
