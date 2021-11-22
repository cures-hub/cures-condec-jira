package de.uhd.ifi.se.decision.management.jira.git.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.CodeComment;
import de.uhd.ifi.se.decision.management.jira.git.model.DecisionKnowledgeElementInCodeComment;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleFromCodeCommentParser {
	private RationaleFromCodeCommentParser rationaleFromCodeCommentExtractor;
	private CodeComment codeComment;
	private int codeCommentBeginLine = 10;
	private List<DecisionKnowledgeElementInCodeComment> elementsFound;

	@Before
	public void setUp() {
		codeComment = new CodeComment("", codeCommentBeginLine, codeCommentBeginLine + 1);
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser();
	}

	@Test
	public void testEmptyComment() {
		assertEquals(0, rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment).size());
	}

	@Test
	public void testCommentWithoutRationaleElement() {
		codeComment.setCommentContent("Text without rationale");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(new ArrayList<KnowledgeElement>(), elementsFound);
	}

	@Test
	public void testOneRationaleElement() {
		codeComment.setCommentContent("Text @issue with rationale");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparetedByNewLinesOnly() {
		codeComment.setCommentContent("Text @issue with rationale\n\n\nnot rat. text anymore");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparatedByLinesWithSpaces() {
		codeComment.setCommentContent("Text @issue with rationale  \n  \n  \nnot rat. text anymore");
		String expectedKey = codeCommentBeginLine + "";
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);

		assertEquals(1, elementsFound.size());
		KnowledgeElement element = elementsFound.get(0);
		assertEquals("with rationale", element.getSummary());
		assertEquals(expectedKey, element.getKey());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparatedByLinesWithSpacesAndTabs() {
		codeComment.setCommentContent("Text @issue with rationale \t \n \t \n \t \nnot rat. text anymore");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementWithinCode() {
		codeComment.setCommentContent("public class GodClass {"
				+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n \t \n}");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(1, elementsFound.size());
		assertEquals("Small code issue in GodClass, it does nothing.", elementsFound.get(0).getSummary());
	}

	@Test
	public void testTwoRationaleElements() {
		codeComment.setCommentContent("@issue Hi @alternative rationale\n\n\nnot rat. text anymore");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(2, elementsFound.size());
		assertEquals("Hi", elementsFound.get(0).getSummary());
		assertEquals("rationale", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsWronglyConcatenated() {
		codeComment.setCommentContent("@issue How to? @alternative We could");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(2, elementsFound.size());
		assertEquals("How to?", elementsFound.get(0).getSummary());
		assertEquals("We could", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsSeparatedByNotRationaleText() {
		codeComment.setCommentContent("@issue #1\n\n\nnot rat. text anymore @issue #2");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(2, elementsFound.size());
		assertEquals("#1", elementsFound.get(0).getSummary());
		assertEquals("#2", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsCheckDecKnowledgeElementKeys() {
		codeComment.setCommentContent("@issue #1\n\n\nnot rat. text anymore @issue #2\n\npart2");
		String expectedKeyEnd = String.valueOf(codeComment.getBeginLine());
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		String foundElementKey = elementsFound.get(0).getKey();

		assertEquals(foundElementKey, expectedKeyEnd);
		assertEquals(2, elementsFound.size());

		expectedKeyEnd = String.valueOf(codeComment.getBeginLine() + 3);
		foundElementKey = elementsFound.get(1).getKey();
		assertEquals(foundElementKey, expectedKeyEnd);
	}

	@Test
	public void testRationaleElementsInMultiLineJavaComment() {
		codeComment.setCommentContent(
				"/** \n" + " * @issue Will this issue be parsed correctly? \n" + " * @alternative We will see!\n"
						+ " * @pro This is a very long argument, so we put it into more than one\n" + " * line. \n"
						+ " * \n" + " * not rat. text anymore\n" + " */");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(3, elementsFound.size());
		assertEquals("Will this issue be parsed correctly?", elementsFound.get(0).getSummary());
		assertEquals("We will see!", elementsFound.get(1).getSummary());
		assertEquals("This is a very long argument, so we put it into more than one line.",
				elementsFound.get(2).getSummary());
	}

	@Test
	public void testRationaleElementsInSingleLineJavaComment() {
		codeComment.setCommentContent(
				"// \n" + "// @issue Will this issue be parsed correctly? \n" + "// @alternative We will see!\n"
						+ "// @pro This is a very long argument, so we put it into more than one\n" + "// line. \n"
						+ "// \n" + "// not rat. text anymore\n" + "//");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(3, elementsFound.size());
		assertEquals("Will this issue be parsed correctly?", elementsFound.get(0).getSummary());
		assertEquals("We will see!", elementsFound.get(1).getSummary());
		assertEquals("This is a very long argument, so we put it into more than one line.",
				elementsFound.get(2).getSummary());
	}

	@Test
	public void testRationaleElementsInPythonComment() {
		codeComment.setCommentContent(
				"# \n" + "# @issue Will this issue be parsed correctly? \n" + "# @alternative We will see!\n"
						+ "# @pro This is a very long argument, so we put it into more than one\n" + "# line. \n"
						+ "# \n" + "# not rat. text anymore\n" + "#");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(3, elementsFound.size());
		assertEquals("Will this issue be parsed correctly?", elementsFound.get(0).getSummary());
		assertEquals("We will see!", elementsFound.get(1).getSummary());
		assertEquals("This is a very long argument, so we put it into more than one line.",
				elementsFound.get(2).getSummary());
	}

	@Test
	public void testGetRationaleTypeByTag() {
		assertEquals(KnowledgeType.DECISION,
				RationaleFromCodeCommentParser.getRationaleTypeFromTag("@decision: We will"));
		assertEquals(KnowledgeType.DECISION,
				RationaleFromCodeCommentParser.getRationaleTypeFromTag("@decision We will"));
	}

	@Test
	@NonTransactional
	public void testTwoDecisionProblemsWithManyDecisionKnowledgeElements() {
		codeComment.setCommentContent("/**" + //
				"* @issue How to present related knowledge and change impact to developers?\n" + //
				"* @alternative Present related knowledge and change impact as a list of\n" + //
				"*              proposals.\n" + //
				"* @con Would mislead developers. Developers associate content assist with\n" + //
				"*      auto-completion and proposals for bug-fixes.\n" + //
				"* @decision Present related knowledge and change impact in dedicated views!\n" + //
				"*\n" + //
				"* @issue How to trigger decision exploration and change impact analysis?\n" + //
				"* @alternative Content assist invocation triggers decision exploration view and\n" + //
				"*              change impact analysis view\n" + //
				"* @con Would mislead developers. Developers associate content assist with\n" + //
				"*      auto-completion and proposals for bug-fixes.\n" + //
				"* @decision Use menu items in context menu to trigger decision exploration and\n" + //
				"*           change impact analysis!" + //
				"*/");
		elementsFound = rationaleFromCodeCommentExtractor.getRationaleElementsFromCodeComment(codeComment);
		assertEquals(8, elementsFound.size());
		assertEquals("Use menu items in context menu to trigger decision exploration and change impact analysis!*",
				elementsFound.get(7).getSummary());
	}

	@Test
	public void testCodeExplanationIsNotExtracted() {
		assertEquals(KnowledgeType.OTHER, RationaleFromCodeCommentParser.getRationaleTypeFromTag("@code Code Example"));
	}
}