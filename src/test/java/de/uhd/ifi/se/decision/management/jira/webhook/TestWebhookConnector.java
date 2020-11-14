package de.uhd.ifi.se.decision.management.jira.webhook;

import java.util.ArrayList;
import java.util.Collection;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestWebhookConnector extends TestSetUp {
	private WebhookConnector webhookConnector;
	private KnowledgeElement element;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		Collection<String> rootTypes = new ArrayList<String>();
		rootTypes.add("DECISION");
		webhookConnector = new WebhookConnector("TEST", "https://CUUSE/conDec", "secret", rootTypes);
		element = new KnowledgeElement(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, null, null);
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyMissingUrlProvidedSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, "1234IamASecretKey", null);
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyProvidedUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, "https://ThisIsTheURL", null, null);
		assertEquals("https://ThisIsTheURL", connector.getUrl());
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testConstructorProvidedProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector("TEST", null, null, null);
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testConstructorWrongProjectKey() {
		WebhookConnector connector = new WebhookConnector("NoTest");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testConstructorCorrectProjectKey() {
		WebhookConnector connector = new WebhookConnector("TEST");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElement(null));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesFails() {
		assertFalse(webhookConnector.sendElement(null, "changed"));
	}

	@Test
	@NonTransactional
	public void testDeleteElementFails() {
		assertFalse(webhookConnector.deleteElement(null, user));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWorks() {
		// Sending does only work for project key "ConDec". Currently, the project key
		// is "TEST".
		assertFalse(webhookConnector.sendElement(element, "changed"));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWrongHTTP() {
		webhookConnector.setUrl("https://wrong");
		assertFalse(webhookConnector.sendElement(element, "changed"));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesWrongResponse() {
		webhookConnector.setUrl("https://jira-se.ifi.uni-heidelberg.de/jira");
		assertFalse(webhookConnector.sendElement(element, "changed"));
	}

	@Test
	@NonTransactional
	public void testSendElementChangesReceiverSlack() {
		webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");

		KnowledgeElement knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);

		assertFalse(webhookConnector.sendElement(element, "changed"));
	}

	@Test
	@NonTransactional
	public void testSendTestReceiverSlack() {
		webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");

		assertFalse(webhookConnector.sendTestPost());
	}

	@Test
	@NonTransactional
	public void testDeleteRootElementInTreeWorks() {
		assertTrue(webhookConnector.deleteElement(element, user));
		KnowledgeGraph.getOrCreate("TEST").addVertex(element);
	}

	@Test
	@NonTransactional
	public void testDeleteOtherElementInTreeWorks() {
		element.setType("DESCRIPTION");
		assertTrue(webhookConnector.deleteElement(element, user));
		KnowledgeGraph.getOrCreate("TEST").addVertex(element);
	}

	@Test
	@NonTransactional
	public void testSetGetUrl() {
		webhookConnector.setUrl("https://ThisIsTheURL");
		assertEquals("https://ThisIsTheURL", webhookConnector.getUrl());
	}

	@Test
	@NonTransactional
	public void testSetUrlGetReceiverSlack() {
		webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");
		assertEquals("https://hooks.slack.com/services/T2E2", webhookConnector.getUrl());
	}

	@Test
	@NonTransactional
	public void testSetUrlGetReceiverOther() {
		webhookConnector.setUrl("https://ThisIsTheURL");
		assertEquals("https://ThisIsTheURL", webhookConnector.getUrl());
	}

	@Test
	@NonTransactional
	public void testSendNewElement() {
		webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");

		KnowledgeElement knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);

		assertFalse(webhookConnector.sendElement(knowledgeElement, "new"));
	}

	@After
	public void tearDown() {
		KnowledgeGraph.instances.clear();
	}
}
