package de.uhd.ifi.se.decision.management.jira.extraction;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

public class TestRationaleFromCodeCommentExtractor {
	private RationaleFromCodeCommentExtractor rationaleFromCodeCommentExtractor;
	private CodeComment codeComment;
	private int commitBeginLine = 10;
	private int commitBeginColumn = 20;
	private List<KnowledgeElement> elementsFound;

	@Before
	public void setUp() {
		codeComment = new CodeComment("", commitBeginColumn, commitBeginLine, commitBeginColumn + 1, commitBeginLine + 1
		/**
		 * end point could be recalculated given contents number of lines and last lines
		 * length, but it does not really matter for testing
		 * RationaleFromCodeCommentExtractor class
		 */
		);
	}

	@Test
	public void testEmptyComment() {
		codeComment.commentContent = "";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		assertEquals(new ArrayList<KnowledgeElement>(), rationaleFromCodeCommentExtractor.getElements());
	}

	@Test
	public void testCommentWithoutRationaleElement() {
		codeComment.commentContent = "Text without rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(new ArrayList<KnowledgeElement>(), elementsFound);
	}

	@Test
	public void testOneRationaleElement() {
		codeComment.commentContent = "Text @issue with rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparetedByNewLinesOnly() {
		codeComment.commentContent = "Text @issue with rationale\n\n\nnot rat. text anymore";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparetedByLinesWithSpaces() {
		codeComment.commentContent = "Text @issue with rationale  \n  \n  \nnot rat. text anymore";
		String expectedKey = commitBeginLine + ":" + commitBeginLine + ":11";

		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();

		assertEquals(1, elementsFound.size());
		KnowledgeElement element = elementsFound.get(0);
		assertEquals("with rationale", element.getSummary());
		assertEquals(expectedKey, element.getKey());
		assertEquals(10, RationaleFromCodeCommentExtractor.getRationaleStartLineInCode(element));
		assertEquals(10, RationaleFromCodeCommentExtractor.getRationaleEndLineInCode(element));
		assertEquals(11, RationaleFromCodeCommentExtractor.getRationaleCursorInCodeComment(element));
	}

	@Test
	public void testOneRationaleElementAndRestTextSeparatedByLinesWithSpacesAndTabs() {
		codeComment.commentContent = "Text @issue with rationale \t \n \t \n \t \nnot rat. text anymore";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("with rationale", elementsFound.get(0).getSummary());
	}

	@Test
	public void testOneRationaleElementWithinCode() {
		codeComment.commentContent = "public class GodClass {"
				+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n \t \n}";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals("Small code issue in GodClass, it does nothing.", elementsFound.get(0).getSummary());
	}

	@Test
	public void testTwoRationaleElements() {
		codeComment.commentContent = "@issue Hi @alternative rationale\n\n\nnot rat. text anymore";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals("Hi", elementsFound.get(0).getSummary());
		assertEquals("rationale", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsSeparatedByNotRationaleText() {
		codeComment.commentContent = "@issue #1\n\n\nnot rat. text anymore @issue #2";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals("#1", elementsFound.get(0).getSummary());
		assertEquals("#2", elementsFound.get(1).getSummary());
	}

	@Test
	public void testTwoRationaleElementsCheckDecKnowledgeElementKeys() {
		codeComment.commentContent = "@issue #1\n\n\nnot rat. text anymore @issue #2\n\npart2";
		String expectedKeyEnd = String.valueOf(codeComment.beginLine + 0) + ":"
				+ String.valueOf(codeComment.beginLine + 0) + ":" + "6";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		String foundElementKey = elementsFound.get(0).getKey();

		assertEquals(foundElementKey, expectedKeyEnd);
		assertEquals(2, elementsFound.size());

		expectedKeyEnd = String.valueOf(codeComment.beginLine + 3) + ":" + String.valueOf(codeComment.beginLine + 5)
				+ ":" + "40";
		foundElementKey = elementsFound.get(1).getKey();
		assertEquals(foundElementKey, expectedKeyEnd);
	}

	@Test
	public void testWrongDocumentationLocation() {
		KnowledgeElement element = new KnowledgeElement();
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertEquals(-1, RationaleFromCodeCommentExtractor.getRationaleStartLineInCode(element));
		assertEquals(-1, RationaleFromCodeCommentExtractor.getRationaleEndLineInCode(element));
		assertEquals(-1, RationaleFromCodeCommentExtractor.getRationaleCursorInCodeComment(element));
	}
}