package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

import javax.xml.bind.annotation.XmlElement;
import java.util.*;

public class Matrix {
    @XmlElement
    private List<DecisionKnowledgeElement> headerElements;

	@XmlElement
	private List<String> headerSummaries;

    @XmlElement
    private List<List<String>> coloredRows;

    public Matrix(String projectKey, List<DecisionKnowledgeElement> decisions) {
		this.headerElements = decisions;
        this.coloredRows = this.getColoredRows(projectKey);

		this.headerSummaries = new ArrayList<>();
		for (DecisionKnowledgeElement decision : this.headerElements) {
			this.headerSummaries.add(decision.getSummary());
		}
    }

	public List<DecisionKnowledgeElement> getHeaderElements() {
		return headerElements;
	}

	public List<List<String>> getColoredRows() {
		return this.coloredRows;
	}

	public List<String> getHeaderSummaries() {
        return this.headerSummaries;
    }

    public List<List<String>> getColoredRows(String projectKey) {
		List<List<String>> coloredRows = new ArrayList<>();
    	KnowledgeGraph graph = KnowledgeGraph.getOrCreate(projectKey);
        Set<Link> links = graph.edgeSet();

        for (DecisionKnowledgeElement sourceDecision : this.headerElements) {
            List<String> row = new ArrayList<>();
			Map<Long, String> linksOfRow = this.getLinksForRow(links, sourceDecision);

            for (DecisionKnowledgeElement targetDecision : this.headerElements) {
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

    private Map<Long, String> getLinksForRow(Set<Link> allEntries, DecisionKnowledgeElement decision) {
        Map<Long, String> linksForRow = new TreeMap<>();
        for (Link link : allEntries) {
            if (link.getSource().getId() == decision.getId()) {
               	linksForRow.put(link.getTarget().getId(), LinkType.getLinkTypeColor(link.getType()));
            }
        }
        return linksForRow;
    }
}
