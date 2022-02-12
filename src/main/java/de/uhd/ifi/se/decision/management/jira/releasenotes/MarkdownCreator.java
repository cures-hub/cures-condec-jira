package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Generate the markdown string for the {@link ReleaseNotes} content based on
 * the {@link ReleaseNotesEntry release note entries}.
 */
public class MarkdownCreator {

	private final static String ICON_PATH = "https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/";

	public String getMarkdownString(FilterSettings filterSettings) {
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		KnowledgeGraph graph = filteringManager.getFilteredGraph();
		String markDownString = "";

		if (filterSettings.getSelectedElement() != null) {
			// only one tree is shown in markdown
			markDownString = getMarkdownString(filterSettings.getSelectedElement(), graph);
		}

		// many trees are shown in markdown
		Set<KnowledgeElement> rootElements = graph.vertexSet();
		filteringManager.getFilterSettings().setKnowledgeTypes(null);
		filteringManager.getFilterSettings().setStatus(null);

		for (KnowledgeElement element : rootElements) {
			filteringManager.getFilterSettings().setSelectedElementObject(element);
			graph = filteringManager.getFilteredGraph();
			markDownString += getMarkdownString(element, graph);
		}
		return markDownString;
	}

	public String getMarkdownString(KnowledgeElement rootElement, KnowledgeGraph graph) {
		StringBuilder stringBuilder = new StringBuilder();
		addElementWithIcon(stringBuilder, rootElement, 0);

		Graph<KnowledgeElement, Link> undirectedGraph = new AsUndirectedGraph<KnowledgeElement, Link>(graph);

		BreadthFirstIterator<KnowledgeElement, Link> breadthFirstIterator = new BreadthFirstIterator<>(undirectedGraph,
				rootElement);
		DepthFirstIterator<KnowledgeElement, Link> depthFirstIterator = new DepthFirstIterator<>(undirectedGraph,
				rootElement);

		while (breadthFirstIterator.hasNext()) {
			breadthFirstIterator.next();
		}

		while (depthFirstIterator.hasNext()) {
			KnowledgeElement childElement = depthFirstIterator.next();

			KnowledgeElement parentElement = breadthFirstIterator.getParent(childElement);
			if (parentElement == null) {
				continue;
			}

			/**
			 * @issue How can we get the depth of an element in the markdown tree?
			 * @decision We use the BreadthFirstIterator::getDepth method to get the depth
			 *           of an element!
			 * @con We build the markdown with a DepthFirstIterator which does not offer a
			 *      method getDepth. We currently traverse the graph twice: first, with a
			 *      breadth first and second, with a depth first iterator, which is not very
			 *      efficient.
			 * @alternative We could use a shortest path algorithm (e.g. Dijkstra) to
			 *              determine the link distance.
			 * @con Might also not be very efficient.
			 */
			int depth = breadthFirstIterator.getDepth(childElement);
			addElementWithIcon(stringBuilder, childElement, depth);
		}

		return stringBuilder.toString();
	}

	public String getMarkdownString(ReleaseNotes releaseNotes) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# ").append(releaseNotes.getTitle()).append("\n");
		String projectKey = releaseNotes.getProjectKey();

		addCategory(stringBuilder, ReleaseNotesCategory.NEW_FEATURES, releaseNotes.getNewFeatures(), projectKey);
		addCategory(stringBuilder, ReleaseNotesCategory.IMPROVEMENTS, releaseNotes.getImprovements(), projectKey);
		addCategory(stringBuilder, ReleaseNotesCategory.BUG_FIXES, releaseNotes.getBugFixes(), projectKey);

		return stringBuilder.toString();
	}

	private void addCategory(StringBuilder stringBuilder, ReleaseNotesCategory category,
			List<ReleaseNotesEntry> entries, String projectKey) {
		if (entries.isEmpty()) {
			return;
		}
		stringBuilder.append("\n## ").append(category.getName()).append("\n");

		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		filterSettings.setOnlyDecisionKnowledgeShown(true);

		for (ReleaseNotesEntry entry : entries) {
			KnowledgeElement rootElement = entry.getElement();
			addJiraIssue(stringBuilder, rootElement);
			filterSettings.setSelectedElementObject(rootElement);
			FilteringManager filteringManager = new FilteringManager(filterSettings);
			KnowledgeGraph filteredGraph = filteringManager.getFilteredGraph();
			stringBuilder.append(getMarkdownString(rootElement, filteredGraph));
		}
	}

	private void addJiraIssue(StringBuilder stringBuilder, KnowledgeElement issue) {
		stringBuilder.append("- ").append(issue.getSummary()).append(" ([").append(issue.getKey()).append("](")
				.append(issue.getUrl()).append("))\n");
	}

	private void addElementWithIcon(StringBuilder stringBuilder, KnowledgeElement element, int depth) {
		for (int i = 0; i < depth; i++) {
			stringBuilder.append("\t");
		}
		stringBuilder.append("- ").append(getIconMarkup(element));
		KnowledgeStatus status = element.getStatus();
		if (!status.getColor().isBlank()) {
			stringBuilder.append(status.toString()).append(": ");
		}
		stringBuilder.append(element.getSummary()).append("\n");
	}

	private String getIconMarkup(KnowledgeElement element) {
		if (element.getType() == KnowledgeType.OTHER) {
			return "";
		}
		return "![" + element.getTypeAsString() + "](" + getIconUrl(element) + ") ";
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