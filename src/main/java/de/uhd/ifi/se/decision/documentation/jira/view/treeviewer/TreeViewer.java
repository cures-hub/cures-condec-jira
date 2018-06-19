package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Graph;
import de.uhd.ifi.se.decision.documentation.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.documentation.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description Creates tree viewer content
 */
public class TreeViewer {

	@XmlElement
	private boolean multiple;

	@XmlElement(name = "check_callback")
	private boolean checkCallback;

	@XmlElement
	private Map<String, Boolean> themes;

	@XmlElement
	private HashSet<Data> data;

	private AbstractPersistenceStrategy strategy;

	private Graph graph;

	public TreeViewer() {
	}

	public TreeViewer(String projectKey) {
		this.setMultiple(false);
		this.setCheckCallback(true);
		this.setThemes(ImmutableMap.of("icons", false));

		graph = new GraphImpl(projectKey);

		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
		List<DecisionKnowledgeElement> decisions = strategy.getDecisions(projectKey);

		HashSet<Data> dataSet = new HashSet<Data>();
		for (DecisionKnowledgeElement decision : decisions) {
			dataSet.add(this.getDataStructure(decision));
		}
		this.makeEachIdUnique(dataSet);
		this.setData(dataSet);
	}

	public Data getDataStructure(DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new Data();
		}
		if (graph == null) {
			graph = new GraphImpl(decisionKnowledgeElement.getProjectKey());
		}
		Data data = new Data(decisionKnowledgeElement);
		List<Data> children = this.getChildren(decisionKnowledgeElement);
		data.setChildren(children);
		return data;
	}

	private List<Data> getChildren(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<Data> children = new ArrayList<>();
		List<DecisionKnowledgeElement> linkedElements = graph.getLinkedElements(decisionKnowledgeElement);
		for (DecisionKnowledgeElement element : linkedElements) {
			Data dataChild = new Data(element);
			List<Data> childrenOfElement = this.getChildren(element);
			dataChild.setChildren(childrenOfElement);
			children.add(dataChild);
		}
		return children;
	}

	private void makeEachIdUnique(HashSet<Data> dataSet) {
		List<String> ids = new ArrayList<>();
		ArrayList<Data> dataList = new ArrayList<>(dataSet);
		for (int index = 0; index < dataList.size(); index++) {
			Data parent = dataList.get(index);
			if (!ids.contains(parent.getId())) {
				ids.add(parent.getId());
				dataList.addAll(parent.getChildren());
			} else {
				parent.setId(index + parent.getId());
				dataList.addAll(parent.getChildren());
			}
		}
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isCheckCallback() {
		return checkCallback;
	}

	public void setCheckCallback(boolean checkCallback) {
		this.checkCallback = checkCallback;
	}

	public Map<String, Boolean> getThemes() {
		return themes;
	}

	public void setThemes(Map<String, Boolean> themes) {
		this.themes = themes;
	}

	public HashSet<Data> getData() {
		return data;
	}

	public void setData(HashSet<Data> data) {
		this.data = data;
	}
}
