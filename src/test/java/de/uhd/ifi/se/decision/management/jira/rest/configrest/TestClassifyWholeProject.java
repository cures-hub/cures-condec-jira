package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.ClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.TestClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestClassifyWholeProject extends TestSetUp {

	private HttpServletRequest request;
	private ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.classifyWholeProject(null, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExistsTrainedClassifier() {
		ConfigPersistenceManager.setTextClassifierEnabled("TEST", true);
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		assertEquals(Status.OK.getStatusCode(), configRest.classifyWholeProject(request, "TEST").getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExistsTextClassifierDisabledForProject() {
		assertEquals(Status.FORBIDDEN.getStatusCode(),
				configRest.classifyWholeProject(request, "TEST").getStatus());
	}
}
