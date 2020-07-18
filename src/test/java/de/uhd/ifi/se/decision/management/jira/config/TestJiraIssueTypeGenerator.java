package de.uhd.ifi.se.decision.management.jira.config;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestJiraIssueTypeGenerator {

	private static JiraIssueTypeGenerator generator;

	@BeforeClass
	public static void setUp() {
		TestSetUp.init();
		generator = new JiraIssueTypeGenerator();
	}

	@Test
	public void testGetValueParamNull() {
		assertEquals(0, generator.getValues(null).size());
	}

	@Test
	public void testGetValueParamEmptyMap() {
		assertEquals(0, generator.getValues(new HashMap<String, String>()).size());
	}
	
	@Test
	public void testGetValueParamNotExisting() {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("test", "test");
		assertEquals(0, generator.getValues(paramMap).size());
	}


	@Test
	public void testGetValueProjectNotExisting() {
		Map<String, GenericValue> paramMap = new HashMap<String, GenericValue>();
		GenericValue value = new MockGenericValue("NONEXITINGPROJECT", (long) 0);
		paramMap.put("project", value);
		assertEquals(0, generator.getValues(paramMap).size());
	}

	@Test
	public void testGetValueParamExisting() {
		Map<String, GenericValue> paramMap = new HashMap<String, GenericValue>();
		GenericValue value = new MockGenericValue("TEST", (long) 1);
		paramMap.put("project", value);
		assertEquals(6, generator.getValues(paramMap).size());
	}

	@Test
	public void testGetJiraIssueTypesZero() {
		assertEquals(6, JiraIssueTypeGenerator.getJiraIssueTypes(1).size());
	}
	
	@Test
	public void testGetJiraIssueTypesByProjectKeyValid() {
		assertEquals(6, JiraIssueTypeGenerator.getJiraIssueTypes("TEST").size());
	}
	
	@Test
	public void testGetJiraIssueTypesByProjectKeyNull() {
		assertEquals(0, JiraIssueTypeGenerator.getJiraIssueTypes(null).size());
	}

	@Test
	public void testGetJiraIssueTypesOk() {
		assertEquals(6, JiraIssueTypeGenerator.getJiraIssueTypes(1).size());
	}

	@Test
	public void testGetJiraIssueTypeNameNull() {
		assertEquals("", JiraIssueTypeGenerator.getJiraIssueTypeName(null));
	}

	@Test
	public void testGetJiraIssueTypeNameEmpty() {
		assertEquals("", JiraIssueTypeGenerator.getJiraIssueTypeName(""));
	}

	@Test
	public void testGetJiraIssueTypeNameFilled() {
		Collection<IssueType> issueTypes = JiraIssueTypeGenerator.getJiraIssueTypes(1);
		for (IssueType type : issueTypes) {
			IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(type.getId());
			assertEquals(issueType.getName(), JiraIssueTypeGenerator.getJiraIssueTypeName(type.getId()));
		}
	}
}
