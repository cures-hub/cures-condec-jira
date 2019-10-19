package de.uhd.ifi.se.decision.management.jira.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFilterExtractor extends TestSetUp {

    private ApplicationUser user;
    private FilterSettings settings;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		settings = new FilterSettingsImpl();
		List<String> doculoco = new ArrayList<>();
		for (DocumentationLocation location : DocumentationLocation.getAllDocumentationLocations()) {
			doculoco.add(location.getName());
		}
		settings.setDocumentationLocations(doculoco);
		settings.setSearchString("TEST");
		settings.setCreatedEarliest(System.currentTimeMillis() - 10000);
		settings.setCreatedLatest(System.currentTimeMillis());
		settings.setProjectKey("TEST");
		settings.setSelectedJiraIssueTypes(KnowledgeType.toList());
		settings.setSelectedJiraIssueStatus(KnowledgeStatus.toList());
	}

    @Test
    @NonTransactional
    public void testConstructorFilterStringNullNullNull() {
        FilterExtractor extractor = new FilterExtractorImpl(null, null, (String) null);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringFilledNullNull() {
        FilterExtractor extractor = new FilterExtractorImpl("TEST", null, (String) null);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringNullFilledNull() {
        FilterExtractor extractor = new FilterExtractorImpl(null, user, (String) null);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringNullNullEmpty() {
        FilterExtractor extractor = new FilterExtractorImpl(null, null, "");
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringNullNullFilledString() {
        String filter = "allopenissues";
        FilterExtractor extractor = new FilterExtractorImpl(null, null, "\\?filter=(.)+" + filter);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringNullNullFilledJQL() {
        FilterExtractor extractor = new FilterExtractorImpl(null, null, "\\?filter=allissues?jql=project=TEST");
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringFilledFilledEmpty() {
        FilterExtractor extractor = new FilterExtractorImpl("TEST", user, "");
        // the empty query will be changed to "allissues", i.e. "type != null"
        assertEquals(8, extractor.getAllElementsMatchingQuery().size());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringFilledFilledString() {
        FilterExtractor extractor = new FilterExtractorImpl("TEST", user, "?filter=allopenissues");
        assertEquals(8, extractor.getAllElementsMatchingQuery().size());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterStringFilledFilledFilledJQL() {
        FilterExtractor extractor = new FilterExtractorImpl("TEST", user, "?jql=project=TEST");
        assertEquals(8, extractor.getAllElementsMatchingQuery().size());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterOwnNullProject() {
        FilterExtractor extractor = new FilterExtractorImpl(null, (FilterSettings) null);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterOwnNullSearch() {
        FilterExtractor extractor = new FilterExtractorImpl(user, (FilterSettings) null);
        assertNull(extractor.getQueryHandler());
    }

    @Test
    @NonTransactional
    public void testConstructorFilterOwnFilled() {
        FilterExtractor extractor = new FilterExtractorImpl(user, new FilterSettingsImpl("TEST", ""));
        // the empty query will be changed to "allissues", i.e. "type != null"
        assertEquals(8, extractor.getAllElementsMatchingQuery().size());
    }

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryEmpty() {
		FilterExtractor extractor = new FilterExtractorImpl("TEST", user, "");
		// the empty query will be changed to "allissues", i.e. "type != null"
		assertEquals(5, extractor.getAllGraphs().size());
	}

	@Test
	@NonTransactional
	public void testGetGraphsMatchingQueryFilled() {
		FilterExtractor extractor = new FilterExtractorImpl("TEST", user, "?jql=project=TEST");
		assertEquals(5, extractor.getAllGraphs().size());
	}

    @Test
    @NonTransactional
    public void testGetFilterSettings() {
        FilterExtractor extractor = new FilterExtractorImpl("Test", user, "?jql=project=TEST");
        assertEquals("?jql=project=TEST", extractor.getFilterSettings().getSearchString());
    }

    @Test
    @NonTransactional
    public void testGetAllElementsMatchingCompareFilterSettingsEmpty() {
        FilterSettings newSettings = new FilterSettingsImpl();
        FilterExtractor extractor = new FilterExtractorImpl(user, newSettings);
        assertEquals(0, extractor.getAllElementsMatchingCompareFilter().size(), 0.0);
    }

	@Test
	@NonTransactional
	public void testGetAllElementsMatchingCompareFilterSettingsFilledCreated() {
		settings.setCreatedLatest((long) -1);
		settings.setCreatedEarliest(System.currentTimeMillis() - 100000);
		FilterExtractor extractor = new FilterExtractorImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingCompareFilter().size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testGetAllElementsMatchingCompareFilterSettingsFilledClosed() {
		settings.setCreatedEarliest((long) -1);
		settings.setCreatedLatest(System.currentTimeMillis() + 1000);
		FilterExtractor extractor = new FilterExtractorImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingCompareFilter().size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testGetAllElementsMatchingCompareFilterSettingsFilled() {
		settings.setCreatedLatest(System.currentTimeMillis() + 1000);
		settings.setCreatedEarliest(System.currentTimeMillis() - 100000);
		FilterExtractor extractor = new FilterExtractorImpl(user, settings);
		assertEquals(5, extractor.getAllElementsMatchingCompareFilter().size(), 0.0);
	}
}
