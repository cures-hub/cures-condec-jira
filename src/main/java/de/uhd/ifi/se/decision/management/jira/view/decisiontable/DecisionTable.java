package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jgrapht.traverse.DepthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;

@XmlRootElement(name = "decisiontable")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecisionTable {

	private KnowledgeGraph graph;
	private String projectKey;
	private AbstractPersistenceManagerForSingleLocation persistenceManager;

	@XmlElement
	private List<KnowledgeElement> issues;

	@XmlElement
	private Map<String, List<KnowledgeElement>> decisionTableData;

	public DecisionTable(String projectKey) {
		this.projectKey = projectKey;
		this.graph = KnowledgeGraph.getOrCreate(this.projectKey);
	}

	// is this necessary? are issues keys always xy-1:xyz for subissues?
	private void updatePersistenceManager(String elementKey) {
		if (elementKey.contains(":")) {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey)
					.getManagerForSingleLocation(DocumentationLocation.JIRAISSUETEXT);
		} else {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey).getJiraIssueManager();
		}
	}

	public void setIssues(String elementKey) {
		issues = new ArrayList<>();
		updatePersistenceManager(elementKey);
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(elementKey);
		Iterator<KnowledgeElement> iterator = new DepthFirstIterator<>(this.graph, rootElement);
		while (iterator.hasNext()) {
			KnowledgeElement elem = iterator.next();
			if (elem.getType().equals(KnowledgeType.ISSUE)) {
				issues.add(elem);
			}
		}
	}

	public void setDecisionTableForIssue(String elementKey) {
		String baseKey = elementKey.substring(0, elementKey.indexOf(":"));
		updatePersistenceManager(elementKey);
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(elementKey);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);
		decisionTableData = new HashMap<>();
		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType().equals(KnowledgeType.ALTERNATIVE) || elem.getType().equals(KnowledgeType.DECISION)) {
				String tmpKey = getKey(baseKey, elem);
				decisionTableData.put(tmpKey, new ArrayList<KnowledgeElement>());
				decisionTableData.get(tmpKey).add(elem);
				getArguments(getKey(baseKey, elem), decisionTableData.get(tmpKey));
			}
		}
	}

	// creates issue key based on base key and current issue id
	private String getKey(String baseKey, KnowledgeElement elem) {
		return String.valueOf(baseKey + ":" + elem.getId());
	}

	private void getArguments(String elementKey, List<KnowledgeElement> arguments) {
		updatePersistenceManager(elementKey);
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(elementKey);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);

		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType().equals(KnowledgeType.PRO) || elem.getType().equals(KnowledgeType.CON)) {
				arguments.add(elem);
			}
		}
	}

	public List<KnowledgeElement> getIssues() {
		return this.issues;
	}

	public Map<String, List<KnowledgeElement>> getDecisionTableData() {
		return this.decisionTableData;
	}
}