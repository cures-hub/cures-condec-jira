package de.uhd.ifi.se.decision.management.jira.view.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class Matrix {
	@XmlElement
	private List<KnowledgeElement> headerElements;

	@XmlElement
	private List<List<String>> coloredRows;

	public Matrix(String projectKey, List<KnowledgeElement> decisions) {
		this.headerElements = decisions;
		this.coloredRows = this.getColoredRows(projectKey);
	}

	public Matrix(FilterSettings filterSettings) {
		this(filterSettings.getProjectKey(), new ArrayList<>());
	}

	public List<KnowledgeElement> getHeaderElements() {
		return headerElements;
	}

	public List<List<String>> getColoredRows() {
		return this.coloredRows;
	}

	public List<List<String>> getColoredRows(String projectKey) {
		List<List<String>> coloredRows = new ArrayList<>();
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(projectKey);
		Set<Link> links = graph.edgeSet();

		for (KnowledgeElement sourceDecision : this.headerElements) {
			List<String> row = new ArrayList<String>();
			Map<Long, String> linksOfRow = this.getLinksForRow(links, sourceDecision);

			for (KnowledgeElement targetDecision : this.headerElements) {
				if (targetDecision.getId() == sourceDecision.getId()) {
					row.add("LightGray");
				} else if (linksOfRow.get(targetDecision.getId()) != null) {
					row.add(linksOfRow.get(targetDecision.getId()));
				} else {
					row.add("White");
				}
			}
			coloredRows.add(row);
		}
		return coloredRows;
	}

	private Map<Long, String> getLinksForRow(Set<Link> links, KnowledgeElement decision) {
		Map<Long, String> linksForRow = new TreeMap<Long, String>();
		for (Link link : links) {
			if (link.getSource().getId() == decision.getId()) {
				linksForRow.put(link.getTarget().getId(), LinkType.getLinkTypeColor(link.getType()));
			}
		}
		return linksForRow;
	}
}