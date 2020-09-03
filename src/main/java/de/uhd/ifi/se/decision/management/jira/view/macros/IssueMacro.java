package de.uhd.ifi.se.decision.management.jira.view.macros;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Macro to mark (i.e. annotate/classifiy) text in the description or comments
 * of a Jira issue as an issue (= decision problem to be solved/question to be
 * answered). Each macro class needs to be added in the atlassian-plugin.xml
 * file.
 */
public class IssueMacro extends AbstractKnowledgeClassificationMacro {

	@Override
	public KnowledgeType getKnowledgeType() {
		return KnowledgeType.ISSUE;
	}
}