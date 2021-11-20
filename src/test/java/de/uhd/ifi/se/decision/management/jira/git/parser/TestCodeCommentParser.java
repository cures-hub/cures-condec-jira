package de.uhd.ifi.se.decision.management.jira.git.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.CodeComment;
import de.uhd.ifi.se.decision.management.jira.git.model.CommentStyleType;

public class TestCodeCommentParser extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testVariousDifferentCommentsAndOtherText() {
		ChangedFile file = new ChangedFile(
				"// @con This is a structure violation, but it should not kill knowledge extraction\n\n"//
						+ "// @goal This is a goal outside an issue, let's see where it lands.\n"//
						+ "// @assumption This is an assumption outside an issue, let's see where it lands.\n\n" //
						+ "// @alternative Here is another structure violation!\n\n"//
						+ "/**\n" //
						+ " * @issue Is this yet another structure violation?\n"//
						+ " * @con It would appear so.\n" //
						+ " * @assumption Here is an assumption inside an issue, let's see where this one lands.\n" //
						+ " * @goal Here is a goal inside an issue, let's see where this one lands.\n"//
						+ " */\n" //
						+ "public class GodClass {\n"//
						+ "//@issue Small code issue in GodClass, it does nothing. \t \n \t \n" //
						+ "/** \n" //
						+ " * @issue Will this issue be parsed correctly? \n" //
						+ " * @alternative We will see!\n" //
						+ " * @pro This is a very long argument, so we put it into more than one\n" //
						+ " * line. \n" //
						+ " * \n" //
						+ " * not rationale text anymore\n" //
						+ " */ \t \n}");
		file.setSummary("example.java");
		file.setProject("TEST");
		assertEquals(CommentStyleType.JAVA_C, file.getCommentStyleType());
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(file);
		assertEquals(6, codeComments.size());
	}

	@Test
	public void testMultiLineCommentInOneLine() {
		String fileContent = "/** @issue Is this yet another structure violation?*/\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.JAVA_C);
		assertEquals(1, codeComments.size());
	}

	@Test
	public void testCommentNeverEnds() {
		String fileContent = "/** @issue Is this yet another structure violation?\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.JAVA_C);
		assertEquals(1, codeComments.size());
		assertEquals(fileContent.trim(), codeComments.get(0).getCommentContent());
	}

	@Test
	public void testRComment() {
		String fileContent = "#@issue How to ...?\n" //
				+ "#@alternative We could...\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.PYTHON);
		assertEquals(1, codeComments.size());
		assertEquals(fileContent.trim(), codeComments.get(0).getCommentContent());
	}

	@Test
	public void testHTMLComment() {
		String fileContent = "<!--@issue How to ...?\n" //
				+ "@alternative We could...-->\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.HTML);
		assertEquals(1, codeComments.size());
		assertEquals(fileContent.trim(), codeComments.get(0).getCommentContent());
	}

	@Test
	public void testMultiLineCommentInStringOfTestCaseSuchAsHere() {
		String fileContent = "\"<!--@issue How to ...?\n" //
				+ "@alternative We could...-->\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.HTML);
		assertEquals(0, codeComments.size());
	}

	@Test
	public void testSingleLineCommentInStringOfTestCaseSuchAsHere() {
		String fileContent = "\"//@issue Small code issue in GodClass\n";
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(fileContent, CommentStyleType.JAVA_C);
		assertEquals(0, codeComments.size());

		assertTrue(parser.isCommentInStringsOfTestCase(fileContent, 1));
		assertFalse(parser.isCommentInStringsOfTestCase(fileContent, 0));
	}
}