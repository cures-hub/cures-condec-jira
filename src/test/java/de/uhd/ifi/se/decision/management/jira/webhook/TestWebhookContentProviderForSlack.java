package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestWebhookContentProviderForSlack extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testcreatePostMethodForSlackForMissingProjectKeyAndMissingElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, (String) null, null, null);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	public void testcreatePostMethodForSlackForMissingProjectKeyAndMissingElementKeyAndProvidedSecretAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, (String) null, "1234IamASecretKey", null);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	public void testcreatePostMethodForSlackForMissingProjectKeyAndProvidedElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, "TEST-14", null, null);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndMissingElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", (String) null, null, null);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForMissingProjectKeyAndMissingElementKeyAndMissingSecretAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, (String) null, null, WebhookType.SLACK);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndMissingElementKeyAndMissingSecretAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", (String) null, null, WebhookType.SLACK);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedElementKeyAndMissingSecretAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", "TEST-14", null, WebhookType.SLACK);
		assertNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedElementAndMissingSecretAndSlackReceiver() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement, null, WebhookType.SLACK);
		assertNotNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedKnowledgeElementAndMissingSecretAndSlackReceiverCutSummary() {
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement1.setSummary("{issue}Summary");
		knowledgeElement1.setDescription("Description");
		knowledgeElement1.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement1, null,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedKnowledgeElementAndMissingSecretAndSlackReceiver() {
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement1.setSummary("Summary");
		knowledgeElement1.setDescription("Description");
		knowledgeElement1.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement1, null,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethodForSlack().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testCreateWebhookDataForSlackNewElement() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement, null, WebhookType.SLACK);

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ "TEST : Neues Entscheidungswissen wurde in Jira dokumentiert:" + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Typ:* :Issue:  Issue"
				+ " \\n *Titel*: Summary\\n'},"
				+ "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '"
				+ knowledgeElement.getUrl() + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack(knowledgeElement, "new"));
	}

	@Test
	@NonTransactional
	public void testCreateWebhookDataForSlackChangedElement() {
		KnowledgeElement knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement, null, WebhookType.SLACK);

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ "TEST : Dieses dokumentierte Entscheidungswissen wurde geändert:" + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Typ:* :Issue:  Issue"
				+ " \\n *Titel*: Summary\\n'},"
				+ "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '"
				+ knowledgeElement.getUrl() + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack(knowledgeElement, "changed"));
	}

  @Test
	@NonTransactional
	public void testCreateWebhookDataForSlackTestElement() {
    KnowledgeElement testElement =new KnowledgeElement(1, "TEST", "i");
		testElement.setType(KnowledgeType.ISSUE);
		testElement.setDescription("Test descirption");
		testElement.setSummary("Test Summary");
		testElement.setKey("TEST");

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", testElement, null, WebhookType.SLACK);

    String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'" + "TEST" + " : "
         + "TESTPOST, changed decision knowledge will be shown like this:" + "'}},"
    		 + "{'type':'section','text':{'type':'mrkdwn','text':'*Typ:* :" + testElement.getType() + ":  "
    		 + testElement.getType() + " \\n *Titel*: " + testElement.getSummary() + "\\n'},"
    		 + "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '" + testElement.getUrl()
    		 + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack(testElement, "test"));
	}






}
