package de.uhd.ifi.se.decision.management.jira.view.macros;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestKnowledgeClassificationMacro extends TestSetUp {

	private RenderContext issueView = new RenderContext();
	private RenderContext wysiwygView = new RenderContext();

	@Before
	public void setUp() {
		init();

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
				"<p style='background-color:#ffffcc; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/issue.png'> This is an issue.</p>",
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
				"<p style='background-color:#fce3be; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/decision.png'> This is a decision.</p>",
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
				"<p style='background-color:#fff6e8; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/alternative.png'> This is an alternative.</p>",
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
				"<p style='background-color:#defade; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_pro.png'> This is a supporting argument.</p>",
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
				"<p style='background-color:#ffe7e7; padding: 3px;'><img src='null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/argument_con.png'> This is an attacking argument.</p>",
				result);

		result = conMacro.execute(null, body, wysiwygView);
		assertEquals("\\{con}<p>This is an attacking argument.</p>\\{con}", result);
	}

	@Test
	public void testGetKnowledgeType() {
		IssueMacro issueMacro = new IssueMacro();
		assertEquals(KnowledgeType.ISSUE, issueMacro.getKnowledgeType());
	}

	@Test
	public void testGetTag() {
		assertEquals("", AbstractKnowledgeClassificationMacro.getTag((String) null));
		assertEquals("", AbstractKnowledgeClassificationMacro.getTag((KnowledgeType) null));
		assertEquals("", AbstractKnowledgeClassificationMacro.getTag(KnowledgeType.OTHER));
	}
}
