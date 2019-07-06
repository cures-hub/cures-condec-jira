package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.treant.TestTreant;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTreant.AoSentenceTestDatabaseUpdater.class)
public class TestGetFilterSettings extends TestSetUpWithIssues {

	private ViewRest viewRest;
	private EntityManager entityManager;
	private FilterSettings filterSettings;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		request = new MockHttpServletRequest();
		String jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		filterSettings = new FilterSettingsImpl("TEST", jql, System.currentTimeMillis() - 100,
				System.currentTimeMillis());
		String[] ktypes = new String[KnowledgeType.toList().size()];
		List<String> typeList = KnowledgeType.toList();
		for (int i = 0; i < typeList.size(); i++) {
			ktypes[i] = typeList.get(i);
		}
		String[] doc = new String[DocumentationLocation.toList().size()];
		List<String> docList = DocumentationLocation.toList();
		for (int i = 0; i < docList.size(); i++) {
			doc[i] = docList.get(i);
		}
		filterSettings.setIssueTypes(ktypes);
		filterSettings.setDocumentationLocation(doc);
	}

	@Test
	public void testRequestNullSearchTermNullElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getFilterSettings(null, null, null).getStatus());
	}
	
	@Test
	public void testRequestFilledSearchTermEmptyElementExistent() {
		assertEquals(Response.Status.OK.getStatusCode(),
				viewRest.getFilterSettings(request, "", "TEST-12").getStatus());
	}
}
