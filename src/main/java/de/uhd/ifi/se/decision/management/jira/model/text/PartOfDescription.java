package de.uhd.ifi.se.decision.management.jira.model.text;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.MutableIssue;

import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfDescriptionImpl;

/**
 * Interface for textual parts (substrings) of the JIRA issue description. These
 * parts can either be relevant decision knowledge elements or irrelevant text.
 */
@JsonDeserialize(as = PartOfDescriptionImpl.class)
public interface PartOfDescription {

	/**
	 * Get the JIRA issue description that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @see MutableIssue
	 * @return JIRA issue description as a String.
	 */
	String getJiraIssueDescription();

}
