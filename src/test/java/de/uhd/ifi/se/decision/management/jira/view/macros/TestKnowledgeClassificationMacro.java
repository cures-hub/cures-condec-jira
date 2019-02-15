package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AlternativeMacro;
import de.uhd.ifi.se.decision.management.jira.view.macros.ConMacro;
import de.uhd.ifi.se.decision.management.jira.view.macros.DecisionMacro;
import de.uhd.ifi.se.decision.management.jira.view.macros.IssueMacro;
import de.uhd.ifi.se.decision.management.jira.view.macros.ProMacro;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestKnowledgeClassificationMacro extends TestSetUpWithIssues {

	private EntityManager entityManager;

	private RenderContext issueView = new RenderContext();
	private RenderContext wysiwygView = new RenderContext();

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		issueView.addParam(IssueRenderContext.WYSIWYG_PARAM, false);
		issueView.addParam("jira.issue", "Test-1");
		wysiwygView.addParam(IssueRenderContext.WYSIWYG_PARAM, true);
		wysiwygView.addParam("jira.issue", "Test-1");
	}

	@Test
	public void testIssueMacro() throws MacroException {
		IssueMacro issueMacro = new IssueMacro();
		assertEquals(RenderMode.allow(RenderMode.F_ALL), issueMacro.getBodyRenderMode());
		assertTrue(issueMacro.hasBody());
		String body = "<p>This is an issue.</p>";
		String result = issueMacro.execute(null, body, issueView);
		assertEquals(
				"<p style='background-color:#F2F5A9; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/issue.png'> This is an issue.</p>",
				result);
		result = issueMacro.execute(null, body, wysiwygView);
		assertEquals("\\{issue}<p>This is an issue.</p>\\{issue}", result);
	}

	@Test
	public void testDecisionMacro() throws MacroException {
		DecisionMacro decisionMacro = new DecisionMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), decisionMacro.getBodyRenderMode());
		assertTrue(decisionMacro.hasBody());

		String body = "<p>This is a decision.</p>";
		String result = decisionMacro.execute(null, body, issueView);
		assertEquals(
				"<p style='background-color:#c5f2f9; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/decision.png'> This is a decision.</p>",
				result);
		result = decisionMacro.execute(null, body, wysiwygView);
		assertEquals("\\{decision}<p>This is a decision.</p>\\{decision}", result);

	}

	@Test
	public void testAlternativeMacro() throws MacroException {
		AlternativeMacro alternativeMacro = new AlternativeMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), alternativeMacro.getBodyRenderMode());
		assertTrue(alternativeMacro.hasBody());

		String body = "<p>This is an alternative.</p>";

		String result = alternativeMacro.execute(null, body, issueView);
		assertEquals(
				"<p style='background-color:#f1ccf9; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/alternative.png'> This is an alternative.</p>",
				result);
		result = alternativeMacro.execute(null, body, wysiwygView);
		assertEquals("\\{alternative}<p>This is an alternative.</p>\\{alternative}", result);
	}

	@Test
	public void testProMacro() throws MacroException {
		ProMacro proMacro = new ProMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), proMacro.getBodyRenderMode());
		assertTrue(proMacro.hasBody());

		String body = "<p>This is a supporting argument.</p>";

		String result = proMacro.execute(null, body, issueView);
		assertEquals(
				"<p style='background-color:#b9f7c0; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_pro.png'> This is a supporting argument.</p>",
				result);

		result = proMacro.execute(null, body, wysiwygView);
		assertEquals("\\{pro}<p>This is a supporting argument.</p>\\{pro}", result);

	}

	@Test
	public void testConMacro() throws MacroException {
		ConMacro conMacro = new ConMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), conMacro.getBodyRenderMode());
		assertTrue(conMacro.hasBody());

		String body = "<p>This is an attacking argument.</p>";

		String result = conMacro.execute(null, body, issueView);
		assertEquals(
				"<p style='background-color:#ffdeb5; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_con.png'> This is an attacking argument.</p>",
				result);

		result = conMacro.execute(null, body, wysiwygView);
		assertEquals("\\{con}<p>This is an attacking argument.</p>\\{con}", result);
	}
}
