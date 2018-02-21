package ut.de.uhd.ifi.se.decdoc.jira.rest.treeviewer;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericEntityException;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decdoc.jira.rest.treeviewer.TreeViewerRest;
import ut.de.uhd.ifi.se.decdoc.jira.TestSetUp;

/**
 * @author Tim Kuchenbuch
 */
public class TestTreeViewerRest extends TestSetUp {
	private TreeViewerRest treeview;
	
	@Before
	public void setUp() {
		treeview= new TreeViewerRest();
		initialisation();
	}
	
	@Test
	public void testProjectKeyNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query Parameter 'projectKey' has been omitted, please add a valid projectKey")).build().getEntity(), treeview.getMessage(null).getEntity());
	}
	
	@Test
	public void testProjectKeyDontExist() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Can not find Project corresponding to given Query Parameter 'projectKey'")).build().getEntity(), treeview.getMessage("NotTEST").getEntity());
	}
	
	@Test
	public void testProjectKeyExists() throws GenericEntityException {
		assertEquals(200, treeview.getMessage("TEST").getStatus());
	}

}
