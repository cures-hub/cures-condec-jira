package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

public class TestConsistencyConfig extends TestConfigSuper {
	protected HttpServletRequest request;
	protected ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testSetConsistencyActivatedTrue() {
		assertEquals(200, configRest.setConsistencyActivated(request, "TEST", "true").getStatus());
	}

	@Test
	public void testSetConsistencyActivatedFalse() {
		assertEquals(200, configRest.setConsistencyActivated(request, "TEST", "false").getStatus());
	}

	@Test
	public void testSetConsistencyActivatedNull() {
		assertEquals(getBadRequestResponse(INVALID_ACTIVATION_NULL).getEntity(), configRest.setConsistencyActivated(request, "TEST", null).getEntity());
	}

	@Test
	public void testSetConsistencyActivatedInvalidInput() {
		assertEquals(getBadRequestResponse(INVALID_ACTIVATION_STRING).getEntity(), configRest.setConsistencyActivated(request, "TEST", "null").getEntity());
	}

	@Test
	public void testSetConsistencyActivatedInvalidProjectKey() {
		assertEquals(getBadRequestResponse(INVALID_PROJECTKEY).getEntity(), configRest.setConsistencyActivated(request, "", "true").getEntity());
	}
}


