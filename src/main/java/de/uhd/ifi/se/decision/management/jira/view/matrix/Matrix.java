package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import java.util.*;

public class Matrix {
    @XmlElement
    private List<String> matrixHeaderRow;

    @XmlElement
    private Map<Long, List<String>> matrixData;

    @JsonIgnore
    private Graph graph;


    public Matrix(String projectKey, List<DecisionKnowledgeElement> allDecisions) {
        this.matrixHeaderRow = new MatrixRow(allDecisions).getHeaderRow();

        this.graph = new GraphImpl(projectKey);
        List<Link> links = this.graph.getAllLinks(allDecisions);

        HashSet<MatrixEntry> entries = new HashSet<>();
        for (Link link : links) {
            entries.add(new MatrixEntry(link.getSourceElement().getId(), link.getDestinationElement().getId(), link.getType()));
        }

        this.matrixData = new HashMap<>();
        for (DecisionKnowledgeElement decision : allDecisions) {
            List<String> row = new MatrixRow(entries, allDecisions, decision).getRow();
            this.matrixData.put(decision.getId(), row);
        }
    }
}
