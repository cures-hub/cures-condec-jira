package de.uhd.ifi.se.decision.management.jira.webhook;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestWebhookConnector extends TestSetUp {
	private WebhookConnector webhookConnector;
	private DecisionKnowledgeElement element;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		Collection<String> rootTypes = new ArrayList<String>();
		rootTypes.add("DECISION");
		webhookConnector = new WebhookConnector("ConDec",
				"https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e", rootTypes);
		element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType("DECISION");
		element.setId(14);
		element.setDescription("Test description");
		element.setKey("TEST-14");
		element.setSummary("Test summary");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	public void testConstructorMissingProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, null, null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorMissingProjectKeyMissingUrlProvidedSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, "1234IamASecretKey", null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorMissingProjectKeyProvidedUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, "https://ThisIsTheURL", null, null);
		assertEquals("https://ThisIsTheURL", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorProvidedProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector("TEST", null, null, null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorWrongProjectKey() {
		WebhookConnector connector = new WebhookConnector("NoTest");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorCorrectProjectKey() {
		WebhookConnector connector = new WebhookConnector("TEST");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testSendElementChangesFails() {
		assertFalse(webhookConnector.sendElementChanges(null));
	}

	@Test
	public void testDeleteElementFails() {
		assertFalse(webhookConnector.deleteElement(null, user));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWorks() {
		assertTrue(webhookConnector.sendElementChanges(element));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWrongHTTP() {
		webhookConnector.setUrl("https://wrong");
		assertFalse(webhookConnector.sendElementChanges(element));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWrongResponse() {
		webhookConnector.setUrl("https://jira-se.ifi.uni-heidelberg.de/jira");
		assertFalse(webhookConnector.sendElementChanges(element));
	}

	@Test
	public void testDeleteRootElementInTreeWorks() {
		assertTrue(webhookConnector.deleteElement(element, user));
	}

	@Test
	public void testDeleteOtherElementInTreeWorks() {
		element.setType("DESCRIPTION");
		assertTrue(webhookConnector.deleteElement(element, user));
	}

	@Test
	public void testSetGetUrl() {
		webhookConnector.setUrl("https://ThisIsTheURL");
		assertEquals("https://ThisIsTheURL", webhookConnector.getUrl());
	}
}
