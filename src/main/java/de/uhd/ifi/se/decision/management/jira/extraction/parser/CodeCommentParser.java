package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

/**
 * Interface to extract the decision knowledge elements from code comments.
 */
public interface CodeCommentParser {

	/**
	 * @return all {@link CodeComment}s with their positions in the source file.
	 */
	List<CodeComment> getComments(ChangedFile inspectedFile);
}