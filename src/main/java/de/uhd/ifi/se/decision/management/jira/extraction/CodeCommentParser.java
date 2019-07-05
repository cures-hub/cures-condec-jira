package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

public interface CodeCommentParser {

	/**
	 * Gets all comments found in the source file
	 *
	 * @return : comments with their positions in the source file.
	 */
	List<CodeComment> getComments(File inspectedFile);
}
