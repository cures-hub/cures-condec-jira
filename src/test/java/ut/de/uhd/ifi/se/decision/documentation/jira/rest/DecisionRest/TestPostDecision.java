package ut.de.uhd.ifi.se.decision.documentation.jira.rest.DecisionRest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionsRest;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import ut.TestSetUp;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestPostDecision extends TestSetUp {
	
	private EntityManager entityManager;
	private DecisionsRest decRest;
	private DecisionKnowledgeElement dec;
	private HttpServletRequest req;
	
	@Before
	public void setUp() {
		decRest= new DecisionsRest();
		initialisation();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
		
		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		dec = new DecisionKnowledgeElement(issue);
		dec.setId(1);
		dec.setProjectKey("TEST");
		dec.setType("Solution");
		req = new MockHttpServletRequest();
	}
	
	@Test
	public void testActionTypeNullReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision(null, null, null).getEntity());
	}
	
	@Test
	public void testActionTypeNullReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision(null, null,dec).getEntity());
	}
	
	@Test
	public void testActionTypeNullReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision(null, req, null).getEntity());
	}
	
	@Test
	public void testActionTypeNullReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision(null, req, dec).getEntity());
	}
	
	@Test
	public void testActionTypecreateReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("create", null, null).getEntity());
	}
	
	@Test
	public void testActionTypecreateReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("create", null,dec).getEntity());
	}
	
	@Test
	public void testActionTypecreateReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("create", req, null).getEntity());
	}
	
	@Test
	public void testActionTypecreateReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(),decRest.postDecision("create", req, dec).getStatus());
	}
	@Test
	public void testActionTypecreateErrorReqFilledDecFilled(){
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals( Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build().getEntity(),decRest.postDecision("create", req, dec).getEntity());
	}
	
	@Test
	public void testActionTypeEditReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("edit", null, null).getEntity());
	}
	
	@Test
	public void testActionTypeEditReqNullDecFilled() {		
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("edit", null,dec).getEntity());
	}
	
	@Test
	public void testActionTypeEditReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("edit", req, null).getEntity());
	}
	
	@Test
	public void testActionTypeEditReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(),decRest.postDecision("edit", req, dec).getStatus());
	}
	
	@Test
	public void testActionTypeEditErrorReqFilledDecFilled() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals( Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build().getEntity(),decRest.postDecision("edit", req, dec).getEntity());
	}
	
	@Test
	public void testActionTypeDeleteReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("delete", null, null).getEntity());
	}
	
	@Test
	public void testActionTypeDeleteReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("delete", null,dec).getEntity());
	}
	
	@Test
	public void testActionTypeDeleteReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("delete", req, null).getEntity());
	}
	
	@Test
	public void testActionTypeDeleteReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Status.OK.getStatusCode(),decRest.postDecision("delete", req, dec).getStatus());
	}
	
	@Test
	public void testActionTypeDeleteErrorReqFilledDecFilled() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build().getEntity(),decRest.postDecision("delete", req, dec).getEntity());
	}
	
	@Test
	public void testActionTypeOtherReqNullDecNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("test", null, null).getEntity());
	}
	
	@Test
	public void testActionTypeOtherReqNullDecFilled() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("test", null,dec).getEntity());
	}
	
	@Test
	public void testActionTypeOtherReqFilledDecNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build().getEntity(),decRest.postDecision("test", req, null).getEntity());
	}
	
	@Test
	public void testActionTypeOtherReqFilledDecFilled() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create', 'edit' or 'delete'")).build().getEntity(),decRest.postDecision("test", req, dec).getEntity());
	}
	
}
