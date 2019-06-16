package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class for DiffViewer's branches node
 */
public class BranchDiff {

	@XmlElement
	private String branchName;

	@XmlElement
	private List<RationaleData> elements;

	public BranchDiff(String branchName, List<DecisionKnowledgeElement> decisionKnowledgeElements) {
		this.branchName = branchName;
		this.elements = new ArrayList<>();
		for (DecisionKnowledgeElement rationale : decisionKnowledgeElements) {
			elements.add(new RationaleData(rationale)); //value
		}
	}

	/* Class mapping DecisionKnowledgeElement to xml */
	private class RationaleData {
		@XmlElement
		private String summary;
		@XmlElement
		private String description;
		@XmlElement
		private String key;
		@XmlElement
		private String type;

		public RationaleData(DecisionKnowledgeElement rationale) {
			summary = rationale.getSummary();
			description = rationale.getDescription();
			key = rationale.getKey();
			type = rationale.getType().toString();
		}
	}
}
