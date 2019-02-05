package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestJiraIssueTypeGenerator extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private JiraIssueTypeGenerator generator;
	@Before
	public void setUp(){
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		generator = new JiraIssueTypeGenerator();
	}

	@Test
	public void testGetValueParamNull(){
		assertEquals(0, generator.getValues(null).size(),0.0);
	}

	@Test
	public void testGetValueParmaEmptyMap(){
		assertEquals(0, generator.getValues(new HashMap()).size(),0.0);
	}

	@Test
	public void testGetValueParamNotExisting(){
		Map paramMap = new HashMap<String, GenericValue>();
		paramMap.put("test", "noprojectKey");
		assertEquals(0, generator.getValues(paramMap).size(),0.0);
	}

	@Test
	public void testGetValueParamExisting(){
		Map paramMap = new HashMap<String, GenericValue>();
		GenericValue value = new MockGenericValue("TEST",(long) 1);
		paramMap.put("project", value);
		assertEquals(12, generator.getValues(paramMap).size(),0.0);
	}

	@Test
	public void testGetJiraIssueTypesZero(){
		assertEquals(0, generator.getJiraIssueTypes(0).size(), 0.0);
	}

	@Test
	public void testGetJiraIssueTypesOk(){
		assertEquals(12, generator.getJiraIssueTypes(1).size(), 0.0);
	}

	@Test
	public void testGetJiraIssueTypeNamesNull(){
		assertEquals("", generator.getJiraIssueTypeName(null));
	}

	@Test
	public void testGetJiraIssueTypeNamesEmpty(){
		assertEquals("", generator.getJiraIssueTypeName(""));
	}

	@Test
	public void testGetJiraIssueTypeNamesFilled(){
		Collection<IssueType> issueTypes = generator.getJiraIssueTypes(1);
		for(IssueType type : issueTypes){
			IssueType issueType = ComponentAccessor.getConstantsManager().getIssueType(type.getId());
			assertEquals(issueType.getName(), generator.getJiraIssueTypeName(type.getId()));
		}
	}
}
