package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.git.CommentStyleType;

public class TestCodeCommentParser extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testCodeCommentParser() {
		ChangedFile file = new ChangedFile(
				"// @" + "con This is a structure violation, but it should not kill knowledge extraction\n" + "\n"
						+ "// @" + "goal This is a goal outside an issue, let's see where it lands.\n" + "// @"
						+ "assumption This is an assumption outside an issue, let's see where it lands.\n" + "\n"
						+ "// @" + "alternative Here is another structure violation!\n" + "\n" + "/**\n" + " * @"
						+ "issue Is this yet another structure violation?\n" + " * @" + "con It would appear so.\n"
						+ " * @" + "assumption Here is an assumption inside an issue, let's see where this one lands.\n"
						+ " * @" + "goal Here is a goal inside an issue, let's see where this one lands.\n" + " */"
						+ "\n" + "public class GodClass {\n" + "//@"
						+ "issue Small code issue in GodClass, it does nothing. \t \n \t \n" + "/** \n" + " * @"
						+ "issue Will this issue be parsed correctly? \n" + " * @" + "alternative We will see!\n"
						+ " * @" + "pro This is a very long argument, so we put it into more than one\n" + " * line. \n"
						+ " * \n" + " * not rationale text anymore\n" + " */ \t \n}");
		file.setSummary("example.java");
		file.setProject("TEST");
		assertEquals(CommentStyleType.JAVA_C, file.getCommentStyleType());
		CodeCommentParser parser = new CodeCommentParser();
		List<CodeComment> codeComments = parser.getComments(file);
		assertEquals(6, codeComments.size());
	}

}
