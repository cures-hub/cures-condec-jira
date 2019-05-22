package de.uhd.ifi.se.decision.management.jira.rest.knowledgerest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;
import org.junit.runner.RunWith;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetSummarizedCode extends TestSetUpGit {

	private EntityManager entityManager;
	private KnowledgeRest knowledgeRest;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate()
				,new MockUserManager());
		super.setUp();
		knowledgeRest = new KnowledgeRest();
	}

	@Test
	public void testElementIdFilledProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.OK.getStatusCode(), knowledgeRest.getSummarizedCode(12, "TEST", "i").getStatus());
	}

	@Test
	public void testElementIdNegativeProjectExistentDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getSummarizedCode(-1, "TEST", "i").getStatus());
	}

	@Test
	public void testElementIdFilledProjectNullDocumentationLocationJiraIssue() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), knowledgeRest.getSummarizedCode(12, null, "i").getStatus());
	}
}