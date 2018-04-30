package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description model class for treeviewer configuration
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

	private PersistenceStrategy strategy;

	public TreeViewer() {
	}

	public TreeViewer(String projectKey) {
		this.setMultiple(false);
		this.setCheckCallback(true);
		this.setThemes(ImmutableMap.of("icons", false));

		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
		List<DecisionKnowledgeElement> decisions = strategy.getDecisions(projectKey);

		HashSet<Data> dataSet = new HashSet<Data>();
		for (DecisionKnowledgeElement decision : decisions) {
			dataSet.add(new Data(decision));
		}
		this.makeEachIdUnique(dataSet);
		this.setData(dataSet);
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
