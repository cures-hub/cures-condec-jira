package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

public class TestRationaleFromCodeCommentParser {
	private RationaleFromCodeCommentParser rationaleFromCodeCommentExtractor;
	private CodeComment codeComment;
	private int commitBeginLine = 10;
	private List<KnowledgeElement> elementsFound;

	@Before
	public void setUp() {
		codeComment = new CodeComment("", commitBeginLine, commitBeginLine + 1
		/**
		 * end point could be recalculated given contents number of lines and last lines
		 * length, but it does not really matter for testing
		 * RationaleFromCodeCommentExtractor class
		 */
		);
	}

	@Test
	public void testEmptyComment() {
		codeComment.setCommentContent("");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		assertEquals(new ArrayList<KnowledgeElement>(), rationaleFromCodeCommentExtractor.getElements());
	}

	@Test
	public void testCommentWithoutRationaleElement() {
		codeComment.setCommentContent("Text without rationale");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(new ArrayList<KnowledgeElement>(), elementsFound);
	}

	@Test
	public void testOneRationaleElement() {
		codeComment.setCommentContent("Text @issue with rationale");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparetedByNewLinesOnly() {
		codeComment.setCommentContent("Text @issue with rationale\n\n\nnot rat. text anymore");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparatedByLinesWithSpaces() {
		codeComment.setCommentContent("Text @issue with rationale  \n  \n  \nnot rat. text anymore");
		String expectedKey = commitBeginLine + ":" + commitBeginLine + ":11";

		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();

		assertEquals(1, elementsFound.size());
		KnowledgeElement element = elementsFound.get(0);
		assertEquals("with rationale", element.getSummary());
		assertEquals(expectedKey, element.getKey());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparatedByLinesWithSpacesAndTabs() {
		codeComment.setCommentContent("Text @issue with rationale \t \n \t \n \t \nnot rat. text anymore");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementWithinCode() {
		codeComment.setCommentContent("public class GodClass {"
				+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n \t \n}");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("Small code issue in GodClass, it does nothing.", elementsFound.get(0).getSummary());
	}

	@Test
	public void testTwoRationaleElements() {
		codeComment.setCommentContent("@issue Hi @alternative rationale\n\n\nnot rat. text anymore");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals("Hi", elementsFound.get(0).getSummary());
		assertEquals("rationale", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsSeparatedByNotRationaleText() {
		codeComment.setCommentContent("@issue #1\n\n\nnot rat. text anymore @issue #2");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals("#1", elementsFound.get(0).getSummary());
		assertEquals("#2", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsCheckDecKnowledgeElementKeys() {
		codeComment.setCommentContent("@issue #1\n\n\nnot rat. text anymore @issue #2\n\npart2");
		String expectedKeyEnd = String.valueOf(codeComment.getBeginLine() + 0) + ":"
				+ String.valueOf(codeComment.getBeginLine() + 0) + ":" + "6";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		String foundElementKey = elementsFound.get(0).getKey();

		assertEquals(foundElementKey, expectedKeyEnd);
		assertEquals(2, elementsFound.size());

		expectedKeyEnd = String.valueOf(codeComment.getBeginLine() + 3) + ":"
				+ String.valueOf(codeComment.getBeginLine() + 3) + ":" + "40";
		foundElementKey = elementsFound.get(1).getKey();
		assertEquals(foundElementKey, expectedKeyEnd);
	}

	@Test
	public void testRationaleElementsInMultiLineJavaComment() {
		codeComment.setCommentContent(
				"/** \n" + " * @issue Will this issue be parsed correctly? \n" + " * @alternative We will see!\n"
						+ " * @pro This is a very long argument, so we put it into more than one\n" + " * line. \n"
						+ " * \n" + " * not rat. text anymore\n" + " */");
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
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
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
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
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentParser(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(3, elementsFound.size());
		assertEquals("Will this issue be parsed correctly?", elementsFound.get(0).getSummary());
		assertEquals("We will see!", elementsFound.get(1).getSummary());
		assertEquals("This is a very long argument, so we put it into more than one line.",
				elementsFound.get(2).getSummary());
	}
}