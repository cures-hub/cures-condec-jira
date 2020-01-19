package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestRationaleFromCodeCommentExtractor {
	private RationaleFromCodeCommentExtractor rationaleFromCodeCommentExtractor;
	private CodeComment codeComment;
	private int commitBeginLine = 10;
	private int commitBeginColumn = 20;
	private List<KnowledgeElement> elementsFound;
	private String expectedRationaleText;

	@Before
	public void setUp() {
		codeComment = new CodeComment("", commitBeginColumn, commitBeginLine, commitBeginColumn + 1, commitBeginLine + 1
		/*
		 * end point could be recalculated given contents number of lines and last lines
		 * length, but it does not really matter for testing
		 * RationaleFromCodeCommentExtractor class
		 */
		);
	}

	@Test
	public void emptyComment() {
		codeComment.commentContent = "";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		assertEquals(new ArrayList<KnowledgeElement>(), rationaleFromCodeCommentExtractor.getElements());
	}

	@Test
	public void commentWithoutRationale() {
		codeComment.commentContent = "Text without rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(new ArrayList<KnowledgeElement>(), elementsFound);
	}

	@Test
	public void oneRationale() {
		codeComment.commentContent = "Text @issue:with rationale";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByNewLinesOnly() {
		codeComment.commentContent = "Text @issue:with rationale\n\n\nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByLinesWithSpaces() {
		codeComment.commentContent = "Text @issue:with rationale  \n  \n  \nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		String expectedKey = commitBeginLine + ":" + commitBeginLine + ":12";

		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();

		assertEquals(1, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
		assertEquals(expectedKey, elementsFound.get(0).getKey());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByLinesWithSpacesAndTabs() {
		codeComment.commentContent = "Text @issue:with rationale \t \n \t \n \t \nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(1, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void twoRationale() {
		codeComment.commentContent = "@issue:Hi @alternative:rationale\n\n\nnot rat. text anymore";
		expectedRationaleText = "Hi";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
		expectedRationaleText = "rationale";
		assertEquals(expectedRationaleText, elementsFound.get(1).getSummary());
	}

	@Test
	public void twoRationaleSeparatedByNotRationaleText() {
		codeComment.commentContent = "@issue:#1\n\n\nnot rat. text anymore @issue: #2";
		expectedRationaleText = "#1";
		rationaleFromCodeCommentExtractor = new RationaleFromCodeCommentExtractor(codeComment);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		assertEquals(2, elementsFound.size());
		assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());

		expectedRationaleText = "#2";
		assertEquals(expectedRationaleText, elementsFound.get(1).getSummary());
	}

	@Test
	public void twoRationaleCheckDecKnowledgeElementKeys() {
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
}