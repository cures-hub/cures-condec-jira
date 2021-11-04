package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Class to generate the markdown string out of the selected jira-issue-keys
 */
public class MarkdownCreator {
	private final ApplicationUser user;
	private final String projectKey;
	private final List<String> additionalConfiguration;
	private final Map<String, List<String>> keysForContent;
	private final String title;

	public MarkdownCreator(ApplicationUser user, String projectKey, Map<String, List<String>> keysForContent2,
			String title, List<String> additionalConfiguration2) {
		this.user = user;
		this.projectKey = projectKey;
		this.keysForContent = keysForContent2;
		this.additionalConfiguration = additionalConfiguration2;
		this.title = title;
	}

	public String getMarkdownString() {
		Set<KnowledgeElement> list = getIssuesFromIssueKeys();
		return generateMarkdownString(list);
	}

	private Set<KnowledgeElement> getIssuesFromIssueKeys() {
		String issueQuery = buildQueryFromIssueKeys(keysForContent);
		// make one jql request and later seperate by bugs, features and improvements
		String query = "?jql=project=" + projectKey + "&& key in(" + issueQuery + ")";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		filterSettings.setOnlyDecisionKnowledgeShown(true);
		FilteringManager extractor = new FilteringManager(projectKey, user, query);
		Set<KnowledgeElement> elementsQueryLinked = extractor.getElementsMatchingFilterSettings();
		return elementsQueryLinked;
	}

	private String buildQueryFromIssueKeys(Map<String, List<String>> keysForContent) {
		String result = "";
		StringBuilder jql = new StringBuilder();

		// create flat
		List<String> uniqueList = new ArrayList<>();
		for (ReleaseNotesCategory category : ReleaseNotesCategory.values()) {
			List<String> issueKeys = keysForContent.get(category.toString());
			if (issueKeys != null && !issueKeys.isEmpty()) {
				issueKeys.forEach(key -> {
					if (!uniqueList.contains(key)) {
						uniqueList.add(key);
					}
				});
			}
		}
		if (!uniqueList.isEmpty()) {
			uniqueList.forEach(key -> {
				jql.append(key);
				jql.append(",");
			});
		}
		if (!jql.toString().isEmpty()) {
			// remove last comma
			result = jql.toString().substring(0, jql.length() - 1);
		}
		return result;
	}

	/**
	 * main function which builds the release-note markdown string
	 *
	 * @param issues
	 *            jira issues
	 * @return markdownString
	 */
	private String generateMarkdownString(Set<KnowledgeElement> issues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(title).append(" \n");
		EnumMap<ReleaseNotesCategory, Boolean> containsTitle = ReleaseNotesCategory.toBooleanMap();
		// iterate for each category and check for each issue if its type corresponds
		// with the mapping
		for (ReleaseNotesCategory cat : ReleaseNotesCategory.values()) {
			issues.forEach(issue -> {
				if (keysForContent.get(cat.toString()).contains(issue.getKey())) {
					// add title once
					if (!containsTitle.get(cat)) {
						stringBuilder.append("## ").append(cat.getName()).append(" \n");
						containsTitle.put(cat, true);
					}
					// add issue title and url
					markdownAddIssue(stringBuilder, issue);
					// add decision knowledge of the issue
					if (additionalConfiguration != null && additionalConfiguration
							.contains(AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE.name())) {
						List<KnowledgeElement> comments = new ArrayList<>();
						issues.forEach(sameIssue -> {
							// check if dk knowledge is in issues which contains the issuekey and is one of
							// types issue or decision
							String sameIssueKey = sameIssue.getKey();
							String issueKey = issue.getKey();
							boolean b1 = sameIssueKey.contains(issueKey);
							boolean b2 = sameIssueKey.contains(":");
							boolean b3 = sameIssueKey.equals(issueKey);
							boolean isIssue = sameIssue.getType() == KnowledgeType.ISSUE;
							boolean isDecision = sameIssue.getType() == KnowledgeType.DECISION;
							if ((b1 && b2 && !b3) && (isIssue || isDecision)) {
								comments.add(sameIssue);
							}
						});
						markdownAddComments(stringBuilder, comments);
					}
				}
			});
			// append new line
			stringBuilder.append("\n");
		}

		addAdditionalConfigurationToMarkDownString(stringBuilder, additionalConfiguration);

		return stringBuilder.toString();
	}

	private void markdownAddComments(StringBuilder stringBuilder, List<KnowledgeElement> dkElements) {
		dkElements.forEach(element -> {
			stringBuilder.append("\t- ").append("![").append(element.getTypeAsString()).append("](")
					.append(element.getType().getIconUrl()).append(")").append(element.getTypeAsString()).append(": ")
					.append(element.getSummary()).append("\n");
		});
	}

	private void markdownAddIssue(StringBuilder stringBuilder, KnowledgeElement issue) {
		stringBuilder.append("- ").append(issue.getSummary()).append(" ([").append(issue.getKey()).append("](")
				.append(issue.getUrl()).append(")) \n");
	}

	private void addAdditionalConfigurationToMarkDownString(StringBuilder stringBuilder,
			List<String> additionalConfigurations) {
		if (additionalConfigurations == null) {
			return;
		}
		additionalConfigurations.forEach(configuration -> {
			stringBuilder.append(AdditionalConfigurationOptions.getMarkdownInstruction(configuration));
		});
	}
}
