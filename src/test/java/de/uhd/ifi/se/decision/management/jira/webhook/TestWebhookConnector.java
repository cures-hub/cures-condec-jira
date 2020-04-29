package de.uhd.ifi.se.decision.management.jira.webhook;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

import net.java.ao.test.jdbc.NonTransactional;

public class TestWebhookConnector extends TestSetUp {
	private WebhookConnector webhookConnector;
	private KnowledgeElement element;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		Collection<String> rootTypes = new ArrayList<String>();
		rootTypes.add("DECISION");
		webhookConnector = new WebhookConnector("TEST",
				"https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e", rootTypes);
		element = new KnowledgeElement(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, null, null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyMissingUrlProvidedSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, null, "1234IamASecretKey", null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorMissingProjectKeyProvidedUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector(null, "https://ThisIsTheURL", null, null);
		assertEquals("https://ThisIsTheURL", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorProvidedProjectKeyMissingUrlMissingSecretMissingRootType() {
		WebhookConnector connector = new WebhookConnector("TEST", null, null, null);
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorWrongProjectKey() {
		WebhookConnector connector = new WebhookConnector("NoTest");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorCorrectProjectKey() {
		WebhookConnector connector = new WebhookConnector("TEST");
		assertEquals("http://true", connector.getUrl());
		assertFalse(connector.sendElementChanges(null));
	}

	@Test
	@NonTransactional
	public void testConstructorCorrectSlackUrl() {
				WebhookConnector connector = new WebhookConnector("TEST",
					"https://hooks.slack.com/services/T2E2",
					"03f90207-73bc-44d9-9848-d3f1f8c8254e", null);
		assertEquals("Slack", connector.getReceiver());
	}
	@Test
	@NonTransactional
	public void testConstructorCorrectOtherUrl() {
				WebhookConnector connector = new WebhookConnector("TEST",
					"https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec",
					"03f90207-73bc-44d9-9848-d3f1f8c8254e", null);
		assertEquals("Other", connector.getReceiver());
	}

	@Test
	@NonTransactional
	public void testSendElementChangesFails() {
		assertFalse(webhookConnector.sendElementChanges(null));
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
		assertFalse(webhookConnector.sendElementChanges(element));
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
	@NonTransactional
	public void testsendElementchangesReceiverSlack(){
	webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");

	KnowledgeElement knowledgeElement = new KnowledgeElement((long) 1, "TEST", "i");
	knowledgeElement.setSummary("Summary");
	knowledgeElement.setDescription("Description");
	knowledgeElement.setType(KnowledgeType.ISSUE);

	assertFalse(webhookConnector.sendElementChanges(knowledgeElement));

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
		assertEquals("Slack", webhookConnector.getReceiver());
	}

	@Test
	@NonTransactional
	public void testSetUrlGetReceiverOther() {
		webhookConnector.setUrl("https://ThisIsTheURL");
		assertEquals("https://ThisIsTheURL", webhookConnector.getUrl());
		assertEquals("Other", webhookConnector.getReceiver());
	}

	@Test
	@NonTransactional
	public void testsendnewElement(){
	webhookConnector.setUrl("https://hooks.slack.com/services/T2E2");

	KnowledgeElement knowledgeElement = new KnowledgeElement((long) 1, "TEST", "i");
	knowledgeElement.setSummary("Summary");
	knowledgeElement.setDescription("Description");
	knowledgeElement.setType(KnowledgeType.ISSUE);

	assertFalse(webhookConnector.sendnewElement(knowledgeElement));

}



	@After
	public void tearDown() {
		KnowledgeGraph.instances.clear();
	}
}
