package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.List;

import org.jgrapht.traverse.DepthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Generate the markdown string for the {@link ReleaseNotes} content based on
 * the {@link ReleaseNotesEntry release note entries}.
 */
public class MarkdownCreator {

	private final static String ICON_PATH = "https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/";
	private ReleaseNotes releaseNotes;

	public MarkdownCreator(ReleaseNotes releaseNotes) {
		this.releaseNotes = releaseNotes;
	}

	public String getMarkdownString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(releaseNotes.getTitle()).append("\n");

		addCategory(stringBuilder, ReleaseNotesCategory.NEW_FEATURES, releaseNotes.getNewFeatures());
		addCategory(stringBuilder, ReleaseNotesCategory.IMPROVEMENTS, releaseNotes.getImprovements());
		addCategory(stringBuilder, ReleaseNotesCategory.BUG_FIXES, releaseNotes.getBugFixes());

		return stringBuilder.toString();
	}

	private void addCategory(StringBuilder stringBuilder, ReleaseNotesCategory category,
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
			addJiraIssue(stringBuilder, rootElement);
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
				addElement(stringBuilder, childElement, currentDepth);
			}
		}
	}

	private void addJiraIssue(StringBuilder stringBuilder, KnowledgeElement issue) {
		stringBuilder.append("- ").append(issue.getSummary()).append(" ([").append(issue.getKey()).append("](")
				.append(issue.getUrl()).append("))\n");
	}

	private void addElement(StringBuilder stringBuilder, KnowledgeElement element, int depth) {
		for (int i = 0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("- ").append("![").append(element.getTypeAsString()).append("](")
				.append(getIconUrl(element)).append(") ").append(element.getSummary()).append("\n");
	}

	/**
	 * @issue How can we include icon images into the release notes?
	 * @decision We use the icon URL of github to include icon images into the
	 *           release notes!
	 * @pro The icons can be seen also by non Jira users. Thus, the release notes
	 *      could be excluded in external systems such as release page on github.
	 * @alternative We could the icon URL on the Jira server.
	 * @con The icons could not be seen by non Jira users.
	 * 
	 * @param element
	 *            {@link KnowledgeElement} to be included into the
	 *            {@link ReleaseNotes}.
	 * @return public URL to the icon image that even non Jira users can access.
	 */
	private String getIconUrl(KnowledgeElement element) {
		return ICON_PATH + element.getType().getIconFileName();
	}
}