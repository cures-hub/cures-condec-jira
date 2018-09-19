package de.uhd.ifi.se.decision.management.jira.webhook;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebhookConnector extends TestSetUp {
	private EntityManager entityManager;
	private WebhookConnector connectorHook;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
		connectorHook = new WebhookConnector (
				"https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e");
	}

	@Test
	public void testSetGetUrl() {
		connectorHook.setUrl("Test-New");
		assertEquals("Test-New", connectorHook.getUrl());
	}

	@Test
	public void testSetGetSecret() {
		connectorHook.setSecret("Test-New");
		assertEquals("Test-New", connectorHook.getSecret());
	}

	@Test
	public void testConstNullNull() {
		WebhookConnector connector = new WebhookConnector(null, null);
		assertEquals("", connector.getUrl());
		assertEquals("", connector.getSecret());
	}

	@Test
    public void testConstNullFilled() {
		WebhookConnector connector = new WebhookConnector(null, "test");
		assertEquals("", connector.getUrl());
		assertEquals("test", connector.getSecret());
	}

	@Test
	public void testConstFilledNull() {
		WebhookConnector connector = new WebhookConnector("test", null);
		assertEquals("test", connector.getUrl());
		assertEquals("", connector.getSecret());
	}

	@Test
	public void testConstFilledFilled() {
		WebhookConnector connector = new WebhookConnector("test", "test");
		assertEquals("test", connector.getUrl());
		assertEquals("test", connector.getSecret());
	}

	@Test
	public void testConstNull() {
		WebhookConnector connector = new WebhookConnector(null);
		assertEquals("", connector.getUrl());
		assertEquals("", connector.getSecret());
	}

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
	public void testSendWebHookForIssueKeyNullNull() {
		assertFalse(connectorHook.postKnowledge(null, null));
	}

	@Test
	public void testSendWebHookForIssueKeyNullEmpty() {
		assertFalse(connectorHook.postKnowledge(null, ""));
	}

	@Test
	public void testSendWebHookForIssueKeyEmptyNull() {
    	assertFalse(connectorHook.postKnowledge("", null));
	}

	@Test
	public void testSendWebHookForIssueKeyEmptyEmpty() {
		assertFalse(connectorHook.postKnowledge("", ""));
	}

	@Test
	public void testSendWebHookForIssueKeyNullFilled() {
		assertFalse(connectorHook.postKnowledge(null, "TEST-12"));
	}

	@Test
	public void testSendWebHookForIssueKeyEmptyFilled() {
		assertFalse(connectorHook.postKnowledge("", "TEST-12"));
	}

    @Test
    public void testSendWebHookForIssueKeyFilledNull() {
		assertFalse(connectorHook.postKnowledge("TEST", null));
	}

	@Test
	public void testSendWebHookForIssueKeyFilledEmpty() {
		assertFalse(connectorHook.postKnowledge("TEST", ""));
	}

	@Test
	public void testSendWebHookForGitHashNullNull() {
		assertFalse(connectorHook.postKnowledge(null, null));
	}

	@Test
	public void testSendWebHookForGitHashNullEmpty() {
		assertFalse(connectorHook.postKnowledge(null, ""));
	}

	@Test
	public void testSendWebHookForGitHashEmptyNull() {
		assertFalse(connectorHook.postKnowledge("", null));
    }

	@Test
	public void testSendWebHookForGitHashEmptyEmpty() {
		assertFalse(connectorHook.postKnowledge("", ""));
	}

	@Test
	public void testSendWebHookForGitHashNullFilled() {
		assertFalse(connectorHook.postKnowledge(null, "TEST-12"));
	}

	@Test
	public void testSendWebHookForGitHashEmptyFilled() {
		assertFalse(connectorHook.postKnowledge("", "TEST-12"));
	}

	@Test
    public void testSendWebHookForGitHashFilledNull() {
		assertFalse(connectorHook.postKnowledge("TEST", null));
	}

	@Test
	public void testSendWebHookForGitHashFilledEmpty() {
		assertFalse(connectorHook.postKnowledge("TEST", ""));
	}
}
