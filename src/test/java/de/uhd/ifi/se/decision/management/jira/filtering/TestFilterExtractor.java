package de.uhd.ifi.se.decision.management.jira.filtering;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestFilterExtractor extends TestSetUpWithIssues {

	private ApplicationUser user;
	private String jql;
	private FilterExtractor filterExtractor;
	private FilterSettings data;

	@Before
	public void setUp() {
		initialization();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		jql = "project%20%3D%20CONDEC%20AND%20assignee%20%3D%20currentUser()%20AND%20resolution%20%3D%20Unresolved%20ORDER%20BY%20updated%20DESC";
		data = new FilterSettingsImpl("TEST", jql, user);
		String[] ktypes = new String[KnowledgeType.toList().size()];
		List<String> typeList = KnowledgeType.toList();
		for (int i = 0; i < typeList.size(); i++) {
			ktypes[i] = typeList.get(i);
		}
		String[] doc = new String[DocumentationLocation.getNamesOfDocumentationLocations().size()];
		List<String> docList = DocumentationLocation.getNamesOfDocumentationLocations();
		for (int i = 0; i < docList.size(); i++) {
			doc[i] = docList.get(i);
		}
		//data.setNamesOfSelectedJiraIssueTypesAsArray(ktypes);
		//data.setDocumentationLocations(doc);
		filterExtractor = new FilterExtractor(user, data);
	}

	@Test
	public void testConstructorFilterStringNullNullNull() {
		FilterExtractor extractor = new FilterExtractor(null, null, (String) null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringFilledNullNull() {
		FilterExtractor extractor = new FilterExtractor("TEST", null, (String) null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullFilledNull() {
		FilterExtractor extractor = new FilterExtractor(null, user, (String) null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullEmpty() {
		FilterExtractor extractor = new FilterExtractor(null, null, "");
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + filter);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringNullNullFilledJQL() {
		FilterExtractor extractor = new FilterExtractor(null, null, "\\?filter=(.)+" + jql);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterStringFilledFilledEmpty() {
		FilterExtractor extractor = new FilterExtractor("TEST", user, "");
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterStringFilledFilledString() {
		String filter = "allopenissues";
		FilterExtractor extractor = new FilterExtractor("TEST", user, "?filter=" + filter);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testConstructorFilterStringFilledFilledFilledJQL() {
		FilterExtractor extractor = new FilterExtractor("Test", user, "?jql=" + jql);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	// Test FilterExtractor constructor with personal filter data

	@Test
	public void testConstructorFilterOwnNullProject() {
		FilterExtractor extractor = new FilterExtractor(null, (FilterSettings) null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterOwnNullSearch() {
		FilterExtractor extractor = new FilterExtractor(user, (FilterSettings) null);
		assertNull(extractor.getFilteredDecisions());
	}

	@Test
	public void testConstructorFilterOwnFilled() {
		FilterExtractor extractor = new FilterExtractor(user, data);
		assertEquals(0.0, extractor.getFilteredDecisions().size(), 0.0);
	}

	@Test
	public void testGetGraphsMatchingQueryNull() {
		assertEquals(0.0, filterExtractor.getGraphsMatchingQuery(null).size(), 0.0);
	}

	@Test
	public void testGetGraphsMatchingQueryEmpty() {
		assertEquals(0.0, filterExtractor.getGraphsMatchingQuery("").size(), 0.0);
	}

	@Test
	public void testGetGraphsMatchingQueryFilled() {
		assertEquals(0.0, filterExtractor.getGraphsMatchingQuery("Test").size(), 0.0);
	}
}
