package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
	public void testSetGetUrl() {
		webhookConnector.setUrl("Test-New");
		assertEquals("Test-New", webhookConnector.getUrl());
	}

	@Test
	public void testSetGetSecret() {
		webhookConnector.setSecret("Test-New");
		assertEquals("Test-New", webhookConnector.getSecret());
	}

	// @Test
	// public void testConstNullNullNull() {
	// WebhookConnector connector = new WebhookConnector(null, null, null, null);
	// assertEquals("", connector.getUrl());
	// assertEquals("", connector.getSecret());
	// }

	// @Test
	// public void testConstNullNullFilled() {
	// WebhookConnector connector = new WebhookConnector(null, null, "test", null);
	// assertEquals("", connector.getUrl());
	// assertEquals("test", connector.getSecret());
	// }

	// @Test
	// public void testConstNullFilledNull() {
	// WebhookConnector connector = new WebhookConnector(null, "test", null, null);
	// assertEquals("test", connector.getUrl());
	// assertEquals("", connector.getSecret());
	// }

	// @Test
	// public void testConstFilledNullNull() {
	// WebhookConnector connector = new WebhookConnector("TEST", null, null, null);
	// assertEquals("", connector.getUrl());
	// assertEquals("", connector.getSecret());
	// }

	// @Test
	// public void testConstFilledNullFilled() {
	// WebhookConnector connector = new WebhookConnector("TEST", null, "test",
	// null);
	// assertEquals("", connector.getUrl());
	// assertEquals("test", connector.getSecret());
	// }

	// @Test
	// public void testConstFilledFilledNull() {
	// WebhookConnector connector = new WebhookConnector("TEST", "test", null,
	// null);
	// assertEquals("test", connector.getUrl());
	// assertEquals("", connector.getSecret());
	// }

	@Test
	public void testConstFilledFilledFilled() {
		WebhookConnector connector = new WebhookConnector("TEST", "test", "test", null);
		assertEquals("test", connector.getUrl());
		assertEquals("test", connector.getSecret());
	}

	@Test
	public void testConstNullFilledFilled() {
		WebhookConnector connector = new WebhookConnector(null, "test", "test", null);
		assertEquals("test", connector.getUrl());
		assertEquals("test", connector.getSecret());
	}

	// @Test
	// public void testConstNull() {
	// WebhookConnector connector = new WebhookConnector(null);
	// assertEquals("", connector.getUrl());
	// assertEquals("", connector.getSecret());
	// }

	@Test
	public void testConstFilledWrong() {
		WebhookConnector connector = new WebhookConnector("NoTest");
		assertEquals("true", connector.getUrl());
		assertEquals("true", connector.getSecret());
	}

	@Test
	public void testConstFilled() {
		WebhookConnector connector = new WebhookConnector("TEST");
		assertEquals("true", connector.getUrl());
		assertEquals("true", connector.getSecret());
	}

	@Test
	public void testSendElementChangesFails() {
		assertFalse(webhookConnector.sendElementChanges(null));
	}

	@Test
	public void testDeleteElementFails() {
		assertFalse(webhookConnector.deleteElement(null));
	}

	@Test
	public void testSendElementChangesWorks() {
		assertTrue(webhookConnector.sendElementChanges(element));
	}

	@Test
	public void testDeleteElementWorks() {
		assertTrue(webhookConnector.deleteElement(element));
	}
}
