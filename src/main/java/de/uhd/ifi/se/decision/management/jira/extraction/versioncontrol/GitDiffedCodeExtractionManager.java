package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * purpose: extract decision knowledge elements from files
 * modified by a sequence of commits.
 *
 * Decision knowledge can be documented in code using
 * following syntax inside a source code comment:
 * """
 * @decKnowledgeTag: knowledge summary text
 * knowledge description text after empty
 *
 * """
 *
 * where [decKnowledgeTag] belongs to set of know Knowledge Types,
 * for example issue, alternative, decision etc.
 * and where empty line denotes the end of the decision element.
 * Empty line is optional, if it would be the last line inside the
 * comment.
 *
 * This class will read necessary files from code repository,
 * delegate comment extraction to specialized classes and
 * extract evolution of knowledge elements.
 */
public class GitDiffedCodeExtractionManager {
	/*
	 * [issue]Modified files may add, modify or delete rationale.
	 * For extraction of rationale on modified files their contents
	 * from the base and changed versions are required.
	 * How should both versions of affected files be accessed?{/issue]
	 *
	 * [decision]Use two gitClientImpl instances each checked out
	 * at different commit of the diff range![/decision]
	 * [pro]no additional checkouts need to be performed by the gitClientImpl[/pro]
	 * [con]requires on more gitClientImpl object in the memory[/con]
	 *
	 * [alternative]Switch between commits using one gitClientImpl
	 * instance[/alternative]
	 * [con]frequent checkouts take time[/con]
	 * [con]requires implementation of another special mode in the
	 * client[/con]
	 *
	 */
	private final GitClient gitClientCheckedOutAtDiffStart;
	private final GitClient gitClientCheckedOutAtDiffEnd;
	private List<CodeExtractionResult> result = new ArrayList<>();

	/*
	 * Passing only the git client checked out at the last commit of the diff
	 * limits knowledge extraction to output of new or modified elements.
	 * Noticing deletions of previously existing elements, will not be possible
	 * to detect.
	 */
	public GitDiffedCodeExtractionManager(Map<DiffEntry, EditList> diffEntries
			, GitClient GitClientAtDiffEnd) {
		gitClientCheckedOutAtDiffStart = null;
		gitClientCheckedOutAtDiffEnd = GitClientAtDiffEnd;
		processEntries(diffEntries);
	}

	/*
	 * TODO: Below constructor will be used later, as the class will evolve.
	 */
	public GitDiffedCodeExtractionManager(Map<DiffEntry, EditList> diffEntries
			, GitClient GitClientAtDiffEnd
			, GitClient GitClientAtDiffStart) {
		gitClientCheckedOutAtDiffStart = GitClientAtDiffStart;
		gitClientCheckedOutAtDiffEnd = GitClientAtDiffEnd;
	}

	private void processEntries(Map<DiffEntry, EditList> diffEntries) {
		for (Map.Entry<DiffEntry, EditList> diffEntry : diffEntries.entrySet()) {
			result.add(processEntry(diffEntry));
		}
	}

	private CodeExtractionResult processEntry(Map.Entry<DiffEntry, EditList> diffEntry) {
		return null;
	}

	private class CodeExtractionResult {
		/* list of elements identified after diff */
		public List<DecisionKnowledgeElement> elementsAdded = new ArrayList<>();
		/* list of old elements somehow affected by the diff */
		public List<DecisionKnowledgeElement> elementsAffected = new ArrayList<>();
	}
}
