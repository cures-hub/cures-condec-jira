package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;

public class TestJiraQueryHandler extends TestSetUpWithIssues {	

	private ApplicationUser user;
	private String jql;
	private FilterSettings data;
	private JiraQueryHandler jiraQueryHandler;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		data = new FilterSettingsImpl("TEST", jql, System.currentTimeMillis() - 100, System.currentTimeMillis());
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
		data.setIssueTypes(ktypes);
		data.setDocumentationLocations(doc);
		jiraQueryHandler = new JiraQueryHandler(user, "TEST", false);
	}
	
	@Test
	public void testGetNamesOfJiraIssueTypesInQuery() {
		List<String> types =  jiraQueryHandler.getNamesOfJiraIssueTypesInQuery("issuetype = Issue");
		assertEquals(1, types.size());
		assertEquals("Issue", types.get(0));
	}

}
