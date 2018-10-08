package de.uhd.ifi.se.decision.management.jira.webhook;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebhookConnector extends TestSetUp {
	private EntityManager entityManager;
	private WebhookConnector webhookConnector;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
		webhookConnector = new WebhookConnector("ConDec",
				"https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e", null);
		element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType("TASK");
		element.setId(1);
		element.setDescription("Test description");
		element.setKey("TEST-1");
		element.setSummary("Test summary");
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
		assertEquals("true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testConstructorCorrectProjectKey() {
		WebhookConnector connector = new WebhookConnector("TEST");
		assertEquals("true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	public void testSendElementChangesFails() {
		assertFalse(webhookConnector.sendElementChanges(null));
	}

	@Test
	public void testDeleteElementFails() {
		assertFalse(webhookConnector.deleteElement(null));
	}

	@Ignore
	public void testSendElementChangesWorks() {
		assertTrue(webhookConnector.sendElementChanges(element));
	}

	@Test
	public void testDeleteRootElementInTreeWorks() {
		assertTrue(webhookConnector.deleteElement(element));
	}

	@Test
	public void testDeleteOtherElementInTreeWorks() {
		element.setType("DESCRIPTION");
		assertTrue(webhookConnector.deleteElement(element));
	}

	@Test
	public void testSetGetUrl() {
		webhookConnector.setUrl("https://ThisIsTheURL");
		assertEquals("https://ThisIsTheURL", webhookConnector.getUrl());
	}
}
