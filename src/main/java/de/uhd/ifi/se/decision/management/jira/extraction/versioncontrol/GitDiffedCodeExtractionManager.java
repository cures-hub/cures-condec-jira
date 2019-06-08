package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;

import java.io.File;
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
 */
public class GitDiffedCodeExtractionManager {
	public GitDiffedCodeExtractionManager(Map<DiffEntry, EditList> diffEntries, File entriesBase) {

	}
}
