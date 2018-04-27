package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.rest.ConfigRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigRest extends TestSetUp {
	private EntityManager entityManager;
	private HttpServletRequest req;
	private ConfigRest confRest;
	
	@Before
	public void setUp() {
		UserManager userManager = new MockDefaultUserManager();
		confRest = new ConfigRest(userManager);
		
		initialization();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
		
		req = new MockHttpServletRequest();
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
	}
	// Testing setActivated
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, null, null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, null, "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, null, "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "TEST", "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "TEST", "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "NotTEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "NotTEST", "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setActivated(null, "NotTEST", "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setActivated(req, null, null).getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setActivated(req, null, "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setActivated(req, null, "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedNull() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build().getEntity(),confRest.setActivated(req, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedTrue() {
		assertEquals( Response.ok().build().getClass(),confRest.setActivated(req, "TEST", "true").getClass());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedFalse() {
		assertEquals( Response.ok().build().getClass(),confRest.setActivated(req, "TEST", "false").getClass());
	}
	
	@Test
	public void testdoPostUserUnauthorized() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.setActivated(req, "NotTEST", "false").getStatus());
	}
	
	@Test
	public void testdoPostUserNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.setActivated(req, "NotTEST", "false").getStatus());
	}
	
	//Testing setIssueStrategy
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategydNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, null, null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, null, "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, null, "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "TEST", "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "TEST", "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "NotTEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "NotTEST", "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.setIssueStrategy(null, "NotTEST", "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setIssueStrategy(req, null, null).getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setIssueStrategy(req, null, "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.setIssueStrategy(req, null, "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null")).build().getEntity(),confRest.setIssueStrategy(req, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyTrue() {
		assertEquals( Response.ok().build().getClass(),confRest.setIssueStrategy(req, "TEST", "true").getClass());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyFalse() {
		assertEquals( Response.ok().build().getClass(),confRest.setIssueStrategy(req, "TEST", "false").getClass());
	}
	
	@Test
	public void testdoPutUserUnauthorized() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.setIssueStrategy(req, "NotTEST", "false").getStatus());
	}
	
	@Test
	public void testdoPutUserNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.setIssueStrategy(req, "NotTEST", "false").getStatus());
	}
}
