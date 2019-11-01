package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertDecisionKnowledgeElement extends TestSetUp {

	protected JiraIssueTextPersistenceManager manager;
	protected PartOfJiraIssueText element;
	protected DecisionKnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(
				ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-3"));
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilledParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentFilled() {
		assertNull(manager.insertDecisionKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentFilled() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentFilled() {
		assertNotNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement,
				JiraUsers.SYS_ADMIN.getApplicationUser(), element));
	}

}
