package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;

public interface CommentSplitter {

	String[] EXCLUDED_TAGS = new String[] { "{code}", "{quote}", "{noformat}", "{panel}" };

	/** List of all knowledge types as tags. Sequence matters! */
	String[] RATIONALE_TAGS = new String[] { "{issue}", "{alternative}", "{decision}", "{pro}", "{con}" };

	/** List of all knowledge types as icons. Sequence matters! */
	String[] RATIONALE_ICONS = new String[] { "(!)", "(?)", "(/)", "(y)", "(n)" };

	String[] EXCLUDED_STRINGS = (String[]) ArrayUtils.addAll(ArrayUtils.addAll(EXCLUDED_TAGS, RATIONALE_TAGS),
			RATIONALE_ICONS);

	Set<KnowledgeType> KNOWLEDGE_TYPES = EnumSet.of(KnowledgeType.DECISION, KnowledgeType.ISSUE, KnowledgeType.PRO,
			KnowledgeType.CON, KnowledgeType.ALTERNATIVE);

	/**
	 * Split comment into sentences.
	 * 
	 * @see Sentence
	 * @param comment
	 *            JIRA issue comment.
	 * @return list of sentence objects.
	 */
	List<Sentence> getSentences(Comment comment);
}