package de.uhd.ifi.se.decision.management.jira.extraction.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.AlternativeMacro;
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.ConMacro;
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.DecisionMacro;
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.IssueMacro;
import de.uhd.ifi.se.decision.management.jira.extraction.view.macros.ProMacro;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestMacro extends TestSetUpWithIssues {

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
	public void testReformatCommentBody() {
		assertEquals("test", IssueMacro.reformatCommentBody("<p>test</p>"));
		assertEquals("test", IssueMacro.reformatCommentBody("<p> test</p>"));
		assertEquals("test", IssueMacro.reformatCommentBody("<p> test </p>"));
		assertEquals("test", IssueMacro.reformatCommentBody("<p> test   </p>"));
		assertEquals("test", IssueMacro.reformatCommentBody("<p>      test   </p>"));
	}

	@Test
	public void testIssueMacro() {
		IssueMacro fm = new IssueMacro();
		assertEquals(RenderMode.allow(RenderMode.F_ALL), fm.getBodyRenderMode());
		assertTrue(fm.hasBody());
		String body = "<p>This is an issue.</p>";
		String result = fm.execute(null, body, issueView);
		assertEquals(
				"<img src=\"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/issue.png\"><span style =  \"background-color:#F2F5A9\">This is an issue.</span>",
				result);
		result = fm.execute(null, body, wysiwygView);
		assertEquals("\\{issue}<p>This is an issue.</p>\\{issue}", result);
	}

	@Test
	public void testDecisionMacro() {
		DecisionMacro fm = new DecisionMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), fm.getBodyRenderMode());
		assertTrue(fm.hasBody());

		String body = "<p>This is a decision.</p>";
		String result = fm.execute(null, body, issueView);
		assertEquals(
				"<img src=\"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/decision.png\"><span  style =  \"background-color:#c5f2f9\">This is a decision.</span>",
				result);
		result = fm.execute(null, body, wysiwygView);
		assertEquals("\\{decision}<p>This is a decision.</p>\\{decision}", result);

	}

	@Test
	public void testAlternativeMacro() {
		AlternativeMacro fm = new AlternativeMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), fm.getBodyRenderMode());
		assertTrue(fm.hasBody());

		String body = "<p>This is an alternative.</p>";

		String result = fm.execute(null, body, issueView);
		assertEquals(
				"<img src=\"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/alternative.png\"><span style =  \"background-color:#f1ccf9\">This is an alternative.</span>",
				result);
		result = fm.execute(null, body, wysiwygView);
		assertEquals("\\{alternative}<p>This is an alternative.</p>\\{alternative}", result);
	}

	@Test
	public void testProMacro() {
		ProMacro fm = new ProMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), fm.getBodyRenderMode());
		assertTrue(fm.hasBody());

		String body = "<p>This is a supporting argument.</p>";

		String result = fm.execute(null, body, issueView);
		assertEquals(
				"<img src=\"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_pro.png\"><span style =  \"background-color:#b9f7c0\">This is a supporting argument.</span>",
				result);

		result = fm.execute(null, body, wysiwygView);
		assertEquals("\\{pro}<p>This is a supporting argument.</p>\\{pro}", result);

	}

	@Test
	public void testConMacro() {
		ConMacro fm = new ConMacro();

		assertEquals(RenderMode.allow(RenderMode.F_ALL), fm.getBodyRenderMode());
		assertTrue(fm.hasBody());

		String body = "<p>This is an attacking argument.</p>";

		String result = fm.execute(null, body, issueView);
		assertEquals(
				"<img src=\"null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_con.png\"><span style =  \"background-color:#ffdeb5\">This is an attacking argument.</span>",
				result);

		result = fm.execute(null, body, wysiwygView);
		assertEquals("\\{con}<p>This is an attacking argument.</p>\\{con}", result);
	}
}
