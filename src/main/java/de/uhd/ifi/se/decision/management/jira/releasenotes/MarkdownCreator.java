package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.List;

import org.jgrapht.traverse.DepthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

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
		stringBuilder.append("# ").append(releaseNotes.getTitle()).append("\n");

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
		filterSettings.setCreateTransitiveLinks(true);

		for (ReleaseNotesEntry entry : entries) {
			KnowledgeElement rootElement = entry.getElement();
			markdownAddIssue(stringBuilder, rootElement);
			filterSettings.setSelectedElementObject(rootElement);
			FilteringManager filteringManager = new FilteringManager(filterSettings);
			KnowledgeGraph filteredGraph = filteringManager.getFilteredGraph();
			DepthFirstIterator<KnowledgeElement, Link> iterator = new DepthFirstIterator<>(
					filteredGraph.toUndirectedGraph(), rootElement);

			while (iterator.hasNext()) {
				KnowledgeElement childElement = iterator.next();
				if (childElement.equals(rootElement)) {
					continue;
				}
				int currentDepth = rootElement.getLinkDistance(childElement, 3);
				markdownAddComments(stringBuilder, childElement, currentDepth);
			}
		}
	}

	private void markdownAddComments(StringBuilder stringBuilder, KnowledgeElement element, int depth) {
		for (int i = 0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("- ").append("![").append(element.getTypeAsString()).append("](")
				.append(element.getType().getIconUrl()).append(") ").append(element.getTypeAsString()).append(": ")
				.append(element.getSummary()).append("\n");
	}

	private void markdownAddIssue(StringBuilder stringBuilder, KnowledgeElement issue) {
		stringBuilder.append("- ").append(issue.getSummary()).append(" ([").append(issue.getKey()).append("](")
				.append(issue.getUrl()).append(")) \n");
	}
}
