package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestWebhookContentProviderForTreant extends TestSetUp {
	protected static final Logger LOGGER = LoggerFactory.getLogger(TestWebhookContentProviderForTreant.class);

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndMissingElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant(null, (String) null, null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndMissingElementKeyAndProvidedSecretAndMissingReceiver() {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant(null, (String) null, "1234IamASecretKey", null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndProvidedElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant(null, "TEST-14", null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForProvidedProjectKeyAndMissingElementKeyAndMissingSecretAndMissingReceiver() {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant("TEST", (String) null, null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testCreatePostMethodForProvidedProjectKeyAndProvidedElementKeyAndProvidedSecretAndOtherReceiver() {
		WebhookContentProviderForTreant provider = new WebhookContentProviderForTreant("TEST", "TEST-14", "1234IamASecretKey",
				WebhookType.TREANT);
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}


	@Test
	public void testCreateHashedPayload() {
		LOGGER.info("testCreateHashedPayload()");
		assertEquals(
				WebhookContentProviderForTreant.createHashedPayload("{\"issueKey\": \"CONDEC-1234\", \"ConDecTree\": "
						+ "{\"nodeStructure\":{\"children\":[],\"text\":{\"title\":\"Test Send\","
						+ "\"desc\":\"CONDEC-1234\"}},\"chart\":{\"container\":\"#treant-container\","
						+ "\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},"
						+ "\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,"
						+ "\"subTreeSeparation\":30}}}", "03f90207-73bc-44d9-9848-d3f1f8c8254e"),
				"e7f0bb82f13286d1afea8cb59f07af829177e2bac8a7af4e883a074851152717");
	}

	@Test
	public void testCreateHashedPayloadWithUmlaut() {
		assertEquals(WebhookContentProviderForTreant.createHashedPayload(
				"{\"issueKey\":\"TEST-29\",\"ConDecTree\":{\"nodeStructure\":{\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[],\"htmlClass\":\"solution\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-31\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Alternative\",\"title\":\"gfx\",\"desc\":\"TEST-31\"},\"htmlId\":10700}],\"htmlClass\":\"solution\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-18\",\"title\":\"äundefinedfgreeygyrsehbnhzrdregsycgfanh\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Alternative\",\"title\":\"zweites22\",\"desc\":\"TEST-18\"},\"htmlId\":10601}],\"htmlClass\":\"decision\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-17\",\"title\":\"undefined\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Decision\",\"title\":\"new decision12\",\"desc\":\"TEST-17\"},\"htmlId\":10600}],\"text\":{\"name\":\"Task\",\"title\":\"a\",\"desc\":\"TEST-29\"}},\"chart\":{\"container\":\"#treant-container\",\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,\"subTreeSeparation\":30}}}",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e"),
				"dfb0bca5dbc85e926dd7bf519a1e4a1636bc534a1d13f623108919585aa26262");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateHashedPayloadEmptyKey() {
		WebhookContentProviderForTreant.createHashedPayload("{\"issueKey\": \"CONDEC-1234\", \"ConDecTree\": "
				+ "{\"nodeStructure\":{\"children\":[],\"text\":{\"title\":\"Test Send\","
				+ "\"desc\":\"CONDEC-1234\"}},\"chart\":{\"container\":\"#treant-container\","
				+ "\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},"
				+ "\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,"
				+ "\"subTreeSeparation\":30}}}", "");
	}
}
