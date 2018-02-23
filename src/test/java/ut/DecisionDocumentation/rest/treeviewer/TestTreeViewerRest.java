package ut.DecisionDocumentation.rest.treeviewer;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.DecisionDocumentation.rest.treeviewer.TreeViewerRest;
import com.google.common.collect.ImmutableMap;

<<<<<<< Updated upstream:src/test/java/ut/DecisionDocumentation/rest/treeviewer/TestTreeViewerRest.java
import ut.testsetup.TestSetUp;
=======
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.TreeViewerRest;
import ut.de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
>>>>>>> Stashed changes:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/rest/treeviewer/TestTreeViewerRest.java

public class TestTreeViewerRest extends TestSetUp {
	private TreeViewerRest treeview;
	
	@Before
	public void setUp() {
		treeview= new TreeViewerRest();
		initialisation();
	}
	
	@Test
	public void testProjectKeyNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query parameter 'projectKey' is not provided, please add a valid projectKey")).build().getEntity(), treeview.getMessage(null).getEntity());
	}
	
	@Test
	public void testProjectKeyDontExist() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'")).build().getEntity(), treeview.getMessage("NotTEST").getEntity());
	}
	
	@Test
	public void testProjectKeyExists() throws GenericEntityException {
		assertEquals(200, treeview.getMessage("TEST").getStatus());
	}

}
