package de.uhd.ifi.se.decision.documentation.jira.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreantRest extends TestSetUp {
	private EntityManager entityManager;

	private ViewRest treantRest;
	
	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";

	@Before
	public void setUp() {
		treantRest = new ViewRest();
		initialization();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testProjectNullIssueKeyNullDepthNull() {
		assertEquals(Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", INVALID_PROJECTKEY)).build()
				.getEntity(), treantRest.getTreant(null, null, null).getEntity());
	}
	//
	// @Test
	// public void testProjectNullIssueKeyFilledDepthNull() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Query parameter 'projectKey' is not provided, please add a valid
	// projectKey")).build().getEntity(),treantRest.getTreant(null, "3",
	// null).getEntity());
	// }
	//
	// @Test
	// public void testProjectNullIssueKeyNullDepthFilled() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Query parameter 'projectKey' is not provided, please add a valid
	// projectKey")).build().getEntity(),treantRest.getTreant(null, null,
	// "3").getEntity());
	// }
	//
	// @Test
	// public void testProjectNullIssueKeyFilledDepthFilled() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Query parameter 'projectKey' is not provided, please add a valid
	// projectKey")).build().getEntity(),treantRest.getTreant(null, "3",
	// "1").getEntity());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyNullDepthNull() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Query parameters 'projectKey' and 'issueKey' do not lead to a valid
	// result")).build().getEntity(),treantRest.getTreant("TEST", null,
	// null).getEntity());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyFilledDepthNull() throws
	// GenericEntityException {
	// assertEquals(200,treantRest.getTreant("TEST", "3", null).getStatus());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyNullDepthFilled() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Query parameters 'projectKey' and 'issueKey' do not lead to a valid
	// result")).build().getEntity(),treantRest.getTreant("TEST", null,
	// "1").getEntity());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyFilledDepthFilled() throws
	// GenericEntityException {
	// assertEquals(200,treantRest.getTreant("TEST", "3", "1").getStatus());
	// }
	//
	// @Test
	// public void testProjectNotExistsIssueKeyNullDepthNull() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Cannot find project for the given query parameter
	// 'projectKey'")).build().getEntity(),treantRest.getTreant("NotTEST", null,
	// null).getEntity());
	// }
	//
	// @Test
	// public void testProjectNotExistsIssueKeyFilledDepthNull() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Cannot find project for the given query parameter
	// 'projectKey'")).build().getEntity(),treantRest.getTreant("NotTEST", "3",
	// null).getEntity());
	// }
	//
	// @Test
	// public void testProjectNotExistsIssueKeyNullDepthFilled() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Cannot find project for the given query parameter
	// 'projectKey'")).build().getEntity(),treantRest.getTreant("NotTEST", null,
	// "1").getEntity());
	// }
	//
	// @Test
	// public void testProjectNotExistsIssueKeyFilledDepthFilled() throws
	// GenericEntityException {
	// assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
	// "Cannot find project for the given query parameter
	// 'projectKey'")).build().getEntity(),treantRest.getTreant("NotTEST", "3",
	// "1").getEntity());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyFilledDepthNoInt() throws
	// GenericEntityException {
	// assertEquals(200,treantRest.getTreant("TEST", "3", "Test").getStatus());
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyFilledAllTypes() throws
	// GenericEntityException {
	// for(long i=2; i<= 16;i++){
	// treantRest.getTreant("TEST", Long.toString(i), "3").getStatus();
	// }
	// }
	//
	// @Test
	// public void testProjectExistsIssueKeyFilledChildElements() throws
	// GenericEntityException {
	// StrategyProvider strategyProvider = new StrategyProvider();
	// PersistenceStrategy strategy = strategyProvider.getStrategy("TEST");
	// Issue issue1 = ComponentAccessor.getIssueManager().getIssueObject((long) 12);
	// Issue issue2 = ComponentAccessor.getIssueManager().getIssueObject((long) 13);
	// strategy.insertDecisionKnowledgeElement(new DecisionKnowledgeElementImpl(issue1),
	// ComponentAccessor.getUserManager().getUserByName("NoFails"));
	// strategy.insertDecisionKnowledgeElement(new DecisionKnowledgeElementImpl(issue2),
	// ComponentAccessor.getUserManager().getUserByName("NoFails"));
	//
	// MockIssueLink issuelink = new MockIssueLink((long)100);
	// LinkImpl link = new LinkImpl(issuelink);
	// strategy.insertLink(link,ComponentAccessor.getUserManager().getUserByName("NoFails"));
	//
	// treantRest.getTreant("TEST","12", "3").getStatus();
	// }
}
