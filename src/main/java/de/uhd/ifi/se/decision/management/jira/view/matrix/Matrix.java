package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;

import javax.xml.bind.annotation.XmlElement;
import java.util.*;

public class Matrix {
    @XmlElement
    private Map<Long, String> headers;

    @XmlElement
    private Map<Long, List<String>> data;

    @XmlElement
    private List<String> headerArray;

    @XmlElement
    private List<List<String>> dataArray;

    public Matrix(String projectKey, List<DecisionKnowledgeElement> allDecisions) {
        this.setHeaders(allDecisions);
        this.setData(projectKey, allDecisions);

        this.setHeaderArray();
        this.setDataArray();
    }

    public Map<Long, String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<DecisionKnowledgeElement> allDecisions) {
        this.headers = new TreeMap<>();
        for (DecisionKnowledgeElement decision : allDecisions) {
            this.headers.put(decision.getId(), decision.getSummary());
        }
    }

    public Map<Long, List<String>> getData() {
        return data;
    }

    public void setData(String projectKey, List<DecisionKnowledgeElement> allDecisions) {
        this.data = new TreeMap<>();
        var allEntries = this.getMatrixEntries(projectKey, allDecisions);

        for (DecisionKnowledgeElement decision : allDecisions) {
            List<String> row = new ArrayList<>();

            for (Map.Entry<Long, String> headerRowDecision : this.getHeaders().entrySet()) {
                if (headerRowDecision.getValue().equals(decision.getSummary())) {
                    row.add("LightGray");
                } else if (this.getEntriesForRow(allEntries, decision).get(headerRowDecision.getKey()) != null) {
                    row.add(this.getEntriesForRow(allEntries, decision).get(headerRowDecision.getKey()));
                } else {
                    row.add("White");
                }
            }
            this.data.put(decision.getId(), row);
        }
    }

    public List<String> getHeaderArray() {
        return headerArray;
    }

    public void setHeaderArray() {
        this.headerArray = new ArrayList<>();
        for (Map.Entry<Long, String> header : this.getHeaders().entrySet()) {
            this.headerArray.add(header.getValue());
        }
    }

    public List<List<String>> getDataArray() {
        return dataArray;
    }

    public void setDataArray() {
        this.dataArray = new ArrayList<>();
        for (Map.Entry<Long, List<String>> row : this.getData().entrySet()) {
            this.dataArray.add(row.getValue());
        }
    }

    private Map<Long, String> getEntriesForRow(HashSet<MatrixEntry> allEntries, DecisionKnowledgeElement decision) {
        Map<Long, String> entriesForRow = new TreeMap<>();
        for (MatrixEntry entry : allEntries) {
            if (entry.getIdOfSourceElement().equals(decision.getId())) {
                entriesForRow.put(entry.getIdOfDestinationElement(), entry.getColor());
            }
        }
        return entriesForRow;
    }

    private HashSet<MatrixEntry> getMatrixEntries(String projectKey, List<DecisionKnowledgeElement> allDecisions) {
        Graph graph = new GraphImpl(projectKey);
        List<Link> links = graph.getAllLinks(allDecisions);
        HashSet<MatrixEntry> entries = new HashSet<>();
        for (Link link : links) {
            entries.add(new MatrixEntry(link.getSourceElement().getId(), link.getDestinationElement().getId(), link.getType()));
        }
        return entries;
    }
}
