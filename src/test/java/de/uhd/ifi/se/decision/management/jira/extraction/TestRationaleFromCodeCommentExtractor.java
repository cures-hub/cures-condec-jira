package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestRationaleFromCodeCommentExtractor {
	private RationaleFromCodeCommentExtractor rationaleFromCodeCommentExtractor;
	private CodeCommentWithRange codeCommentWithRange;
	private int commitBeginLine = 10;
	private int commitBeginColumn = 20;
	private ArrayList<DecisionKnowledgeElement> elementsFound;
	private String expectedRationaleText;

	@Before
	public void setUp() {
		codeCommentWithRange = new CodeCommentWithRange(""
				, commitBeginLine, commitBeginColumn
				, commitBeginLine + 1, commitBeginColumn + 1
		/* end point could be recalculated given contents number of lines
		 and last lines length, but it does not really matter for
		 testing RationaleFromCodeCommentExtractor class */
		);
	}

	@Test
	public void emptyComment() {
		codeCommentWithRange.commentContent = "";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		Assert.assertEquals(new ArrayList<DecisionKnowledgeElement>()
				, rationaleFromCodeCommentExtractor.getElements());
	}

	@Test
	public void commentWithoutRationale() {
		codeCommentWithRange.commentContent = "Text without rationale";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(new ArrayList<DecisionKnowledgeElement>()
				, elementsFound);
	}

	@Test
	public void oneRationale() {
		codeCommentWithRange.commentContent = "Text @issue:with rationale";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(1, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByNewLinesOnly() {
		codeCommentWithRange.commentContent = "Text @issue:with rationale\n\n\nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(1, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByLinesWithSpaces() {
		codeCommentWithRange.commentContent = "Text @issue:with rationale  \n  \n  \nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(1, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void oneRationaleAndRestTextSeparetedByLinesWithSpacesAndTabs() {
		codeCommentWithRange.commentContent = "Text @issue:with rationale \t \n \t \n \t \nnot rat. text anymore";
		expectedRationaleText = "with rationale";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(1, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
	}

	@Test
	public void twoRationale() {
		codeCommentWithRange.commentContent = "@issue:Hi @alternative:rationale\n\n\nnot rat. text anymore";
		expectedRationaleText = "Hi";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(2, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());
		expectedRationaleText = "rationale";
		Assert.assertEquals(expectedRationaleText, elementsFound.get(1).getSummary());
	}

	@Test
	public void twoRationaleSeparatedByNotRationaleText() {
		codeCommentWithRange.commentContent = "@issue:#1\n\n\nnot rat. text anymore @issue: #2";
		expectedRationaleText = "#1";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		Assert.assertEquals(2, elementsFound.size());
		Assert.assertEquals(expectedRationaleText, elementsFound.get(0).getSummary());

		expectedRationaleText = "#2";
		Assert.assertEquals(expectedRationaleText, elementsFound.get(1).getSummary());
	}

	@Test
	public void twoRationaleCheckDecKnowledgeElementKeys() {
		codeCommentWithRange.commentContent = "@issue:#1\n\n\nnot rat. text anymore @issue: #2\n\npart2";
		String expectedKeyEnd = String.valueOf(codeCommentWithRange.beginLine + 0) +
				"_" +
				String.valueOf(codeCommentWithRange.beginLine + 0) +
				"_" +
				"7";
		rationaleFromCodeCommentExtractor =
				new RationaleFromCodeCommentExtractor(codeCommentWithRange);
		elementsFound = rationaleFromCodeCommentExtractor.getElements();
		String foundElementKey = elementsFound.get(0).getKey();

		Assert.assertEquals(2, elementsFound.size());
		Assert.assertTrue(foundElementKey.endsWith(expectedKeyEnd));

		expectedKeyEnd = String.valueOf(codeCommentWithRange.beginLine + 3) +
				"_" +
				String.valueOf(codeCommentWithRange.beginLine + 5) +
				"_" +
				"41";
		foundElementKey = elementsFound.get(1).getKey();
		Assert.assertTrue(foundElementKey.endsWith(expectedKeyEnd));
	}
}