package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Class to generate the markdown string out of the selected jira-issue-keys
 */
public class MarkdownCreator {
	private ReleaseNotes releaseNotes;

	public MarkdownCreator(ReleaseNotes releaseNotes) {
		this.releaseNotes = releaseNotes;
	}

	public String getMarkdownString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(releaseNotes.getTitle()).append(" \n");

		markdownAddCategory(stringBuilder, ReleaseNotesCategory.NEW_FEATURES, releaseNotes.getNewFeatures());
		markdownAddCategory(stringBuilder, ReleaseNotesCategory.IMPROVEMENTS, releaseNotes.getImprovements());
		markdownAddCategory(stringBuilder, ReleaseNotesCategory.BUG_FIXES, releaseNotes.getBugFixes());

		return stringBuilder.toString();
	}

	private void markdownAddCategory(StringBuilder stringBuilder, ReleaseNotesCategory category,
			List<ReleaseNotesEntry> entries) {
		if (entries.isEmpty()) {
			return;
		}
		stringBuilder.append("## ").append(category.getName()).append(" \n");

		FilterSettings filterSettings = new FilterSettings(releaseNotes.getProjectKey(), "");
		filterSettings.setOnlyDecisionKnowledgeShown(true);

		for (ReleaseNotesEntry entry : entries) {
			markdownAddIssue(stringBuilder, entry.getElement());
			filterSettings.setSelectedElementObject(entry.getElement());
			FilteringManager filteringManager = new FilteringManager(filterSettings);
			Set<KnowledgeElement> linkedDecisionKnowledge = filteringManager.getElementsMatchingFilterSettings();
			// linkedDecisionKnowledge.removeIf(element ->
			// element.equals(entry.getElement()));
			markdownAddComments(stringBuilder, new ArrayList<>(linkedDecisionKnowledge));
		}
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
}
