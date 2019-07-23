package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;

public class TestJiraIssueTypeGenerator extends TestSetUpWithIssues {

	private JiraIssueTypeGenerator generator;

	@Before
	public void setUp() {
		initialization();

		generator = new JiraIssueTypeGenerator();
	}

	@Test
	public void testGetValueParamNull() {
		assertEquals(0, generator.getValues(null).size(), 0.0);
	}

	@Test
	public void testGetValueParmaEmptyMap() {
		assertEquals(0, generator.getValues(new HashMap<String, String>()).size(), 0.0);
	}

	@Test
	public void testGetValueParamNotExisting() {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("test", "noprojectKey");
		assertEquals(0, generator.getValues(paramMap).size(), 0.0);
	}

	@Test
	@Ignore
	public void testGetValueParamExisting() {
		Map<String, GenericValue> paramMap = new HashMap<String, GenericValue>();
		GenericValue value = new MockGenericValue("TEST", (long) 1);
		paramMap.put("project", value);
		assertEquals(12, generator.getValues(paramMap).size(), 0.0);
	}

	@Test
	public void testGetJiraIssueTypesZero() {
		assertEquals(0, JiraIssueTypeGenerator.getJiraIssueTypes(0).size(), 0.0);
	}

	@Test
	@Ignore
	public void testGetJiraIssueTypesOk() {
		assertEquals(12, JiraIssueTypeGenerator.getJiraIssueTypes(1).size(), 0.0);
	}

	@Test
	public void testGetJiraIssueTypeNamesNull() {
		assertEquals("", JiraIssueTypeGenerator.getJiraIssueTypeName(null));
	}

	@Test
	public void testGetJiraIssueTypeNamesEmpty() {
		assertEquals("", JiraIssueTypeGenerator.getJiraIssueTypeName(""));
	}

	@Test
	public void testGetJiraIssueTypeNamesFilled() {
		Collection<IssueType> issueTypes = JiraIssueTypeGenerator.getJiraIssueTypes(1);
		for (IssueType type : issueTypes) {
			IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(type.getId());
			assertEquals(issueType.getName(), JiraIssueTypeGenerator.getJiraIssueTypeName(type.getId()));
		}
	}
}
