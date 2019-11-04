package de.uhd.ifi.se.decision.management.jira.releasenotes;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * Class to generate the markdown string out of the selected jira-issue-keys
 */
public class MarkdownCreator {
	private final String pathToIcons = "https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/";
	private final ApplicationUser user;
	private final String projectKey;
	private final ArrayList<String> additionalConfiguration;
	private final HashMap<String, ArrayList<String>> keysForContent;
	private final String title;

	public MarkdownCreator(ApplicationUser user, String projectKey, HashMap<String, ArrayList<String>> keysForContent, String title, ArrayList<String> additionalConfiguration) {
		this.user = user;
		this.projectKey = projectKey;
		this.keysForContent = keysForContent;
		this.additionalConfiguration = additionalConfiguration;
		this.title = title;
	}

	public String getMarkdownString() {
		List<DecisionKnowledgeElement> list = getIssuesFromIssueKeys();
		return generateMarkdownString(list);
	}

	private List<DecisionKnowledgeElement> getIssuesFromIssueKeys() {
		String issueQuery = buildQueryFromIssueKeys(keysForContent);
		//make one jql request and later seperate by bugs, features and improvements
		String query = "?jql=project=" + projectKey + "&& key in(" + issueQuery + ")";
		FilterExtractor extractor = new FilterExtractorImpl(projectKey, user, query);
		List<DecisionKnowledgeElement> elementsQueryLinked = new ArrayList<DecisionKnowledgeElement>();
		elementsQueryLinked = extractor.getAllElementsMatchingQuery();
		return elementsQueryLinked;
	}


	private String buildQueryFromIssueKeys(HashMap<String, ArrayList<String>> keysForContent) {
		String result = "";
		StringBuilder jql = new StringBuilder();

		List<String> categories = ReleaseNoteCategory.toList();
		//create flat
		List<String> uniqueList = new ArrayList<>();
		categories.forEach(category -> {
			ArrayList<String> issueKeys = keysForContent.get(category);
			if (issueKeys != null && !issueKeys.isEmpty()) {
				issueKeys.forEach(key -> {
					if (!uniqueList.contains(key)) {
						uniqueList.add(key);
					}
				});
			}
		});
		if (!uniqueList.isEmpty()) {
			uniqueList.forEach(key -> {
				jql.append(key);
				jql.append(",");
			});
		}
		if (!jql.toString().isEmpty()) {
			//remove last comma
			result = jql.toString().substring(0, jql.length() - 1);
		}
		return result;
	}


	/**
	 * main function which builds the release-note markdown string
	 *
	 * @param issues jira issues
	 * @return markdownString
	 */
	private String generateMarkdownString(List<DecisionKnowledgeElement> issues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(title).append(" \n");
		EnumMap<ReleaseNoteCategory, Boolean> containsTitle = ReleaseNoteCategory.toBooleanMap();
		//iterate for each category and check for each issue if its type corresponds with the mapping
		ReleaseNoteCategory.toList().forEach(cat -> {
			issues.forEach(issue -> {
				if (keysForContent.get(cat).contains(issue.getKey())) {
					//add title once
					if (!containsTitle.get(ReleaseNoteCategory.getTargetGroup(cat))) {
						stringBuilder.append("## ")
								.append(ReleaseNoteCategory.getTargetGroupReadable(ReleaseNoteCategory.getTargetGroup(cat)))
								.append(" \n");
						containsTitle.put(ReleaseNoteCategory.getTargetGroup(cat), true);
					}
					//add issue title and url
					markdownAddIssue(stringBuilder, issue);
					//add decision knowledge of the issue
					if (additionalConfiguration != null && additionalConfiguration.contains(AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE.toUpperString())) {
						List<DecisionKnowledgeElement> comments = new ArrayList<DecisionKnowledgeElement>();
						issues.forEach(sameIssue -> {
							//check if dk knowledge is in issues which contains the issuekey and is one of types issue or decision
							String sameIssueKey = sameIssue.getKey();
							String issueKey = issue.getKey();
							Boolean b1 = sameIssueKey.contains(issueKey);
							Boolean b2 = sameIssueKey.contains(":");
							Boolean b3 = sameIssueKey.equals(issueKey);
							Boolean isIssue = sameIssue.getType().equals(KnowledgeType.ISSUE);
							Boolean isDecision = sameIssue.getType().equals(KnowledgeType.DECISION);
							if ((b1 && b2 && !b3) && (isIssue || isDecision)) {
								comments.add(sameIssue);
							}
						});
						markdownAddComments(stringBuilder, comments);
					}
				}
			});
			//append new line
			stringBuilder.append("\n");
		});

		addAdditionalConfigurationToMarkDownString(stringBuilder, additionalConfiguration);

		return stringBuilder.toString();
	}

	private void markdownAddComments(StringBuilder stringBuilder, List<DecisionKnowledgeElement> dkElements) {
		dkElements.forEach(element -> {
			String iconUrl = "decision.png";
			if (element.getType().equals(KnowledgeType.ISSUE)) {
				iconUrl = "issue.png";
			}
			stringBuilder.append("\t- ")
					.append("![")
					.append(element.getTypeAsString())
					.append("](")
					.append(pathToIcons)
					.append(iconUrl)
					.append(")")
					.append(element.getTypeAsString())
					.append(": ")
					.append(element.getSummary())
					.append("\n");
		});
	}

	private void markdownAddIssue(StringBuilder stringBuilder, DecisionKnowledgeElement issue) {
		stringBuilder.append("- ")
				.append(issue.getSummary())
				.append(" ([")
				.append(issue.getKey())
				.append("](")
				.append(issue.getUrl())
				.append(")) \n");
	}

	private void addAdditionalConfigurationToMarkDownString(StringBuilder stringBuilder, ArrayList<String> additionalConfiguration) {
		if (additionalConfiguration != null) {
			additionalConfiguration.forEach(type -> {
				stringBuilder.append(AdditionalConfigurationOptions.getMarkdownOptionsString(type));
			});

		}
	}

}
