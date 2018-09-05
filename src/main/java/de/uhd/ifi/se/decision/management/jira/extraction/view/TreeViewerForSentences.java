package de.uhd.ifi.se.decision.management.jira.extraction.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.connector.ViewConnector;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.SentenceExtractionGraphImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.Data;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;

public class TreeViewerForSentences extends TreeViewer {

	public TreeViewerForSentences() {
		super();
	}

	public TreeViewerForSentences(String issueId, boolean showRelevant) {
		super();
		Issue currentIssue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		ViewConnector vc = new ViewConnector(currentIssue, true);

		Set<Data> dataSet = new HashSet<Data>();
		for (Sentence sentence : vc.getAllSentenceInstances(false)) {
			// Check to not display elements that are child elements for other elements
			if (ActiveObjectsManager.getInwardLinks(sentence).size() == 0) {
				if(!sentence.getBody().contains("{code}") && !sentence.getBody().contains("{noformat}")) {
					if (!showRelevant && sentence.isRelevant()) {
						dataSet.add(this.getDataStructureWithSentenceGraph(sentence));
					}
					if (showRelevant) {
						dataSet.add(this.getDataStructureWithSentenceGraph(sentence));
					}
				}
			}
		}
		this.data = dataSet;
	}

	public Data getDataStructureWithSentenceGraph(Sentence decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new Data();
		}
		this.graph = new SentenceExtractionGraphImpl(decisionKnowledgeElement);
		decisionKnowledgeElement.setDescription("");//must be empty to not cause an error by adding additional attributes in the data constructor
		Data data = new Data(decisionKnowledgeElement);
		data = checkIcons(decisionKnowledgeElement,data);
		data = this.makeIdUnique(data);
		List<Data> children = this.getChildrenOfSentences(decisionKnowledgeElement);
		data.setChildren(children);
		return data;
	}
	
	private Data checkIcons(Sentence decisionKnowledgeElement, Data data) {
		if(decisionKnowledgeElement.getArgument().equals("Pro")) {
			data.setIcon(ComponentGetter.getUrlOfImageFolder() + "argument_pro.png");
		}else if(decisionKnowledgeElement.getArgument().equals("Con")){
			data.setIcon(ComponentGetter.getUrlOfImageFolder() + "argument_con.png");
		}
		return data;
	}

	private List<Data> getChildrenOfSentences(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Data> children = new ArrayList<>();
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = this.graph
				.getLinkedElementsAndLinks(decisionKnowledgeElement);

		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			Data dataChild = new Data(childAndLink.getKey(), childAndLink.getValue());
			dataChild = this.makeIdUnique(dataChild);
			List<Data> childrenOfElement = this.getChildren(childAndLink.getKey());
			dataChild.setChildren(childrenOfElement);
			children.add(dataChild);
		}
		return children;
	}

}
