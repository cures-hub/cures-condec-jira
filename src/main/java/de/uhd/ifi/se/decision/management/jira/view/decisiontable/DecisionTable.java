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

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

@XmlRootElement(name = "decisiontable")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecisionTable {

	private KnowledgeGraph graph;
	private String projectKey;
	private KnowledgePersistenceManager persistenceManager;

	@XmlElement
	private List<KnowledgeElement> issues;

	@XmlElement
	private Map<Long, List<KnowledgeElement>> decisionTableData;

	public DecisionTable(String projectKey) {
		this.projectKey = projectKey;
		this.graph = KnowledgeGraph.getOrCreate(this.projectKey);
		persistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
	}

	public void setIssues(String id) {
		issues = new ArrayList<>();
		KnowledgeElement rootElement = persistenceManager.getJiraIssueManager().getKnowledgeElement(id);
		Iterator<KnowledgeElement> iterator = new DepthFirstIterator<>(this.graph, rootElement);
		while (iterator.hasNext()) {
			KnowledgeElement elem = iterator.next();
			if (elem.getType().equals(KnowledgeType.ISSUE)) {
				issues.add(elem);
			}
		}
	}

	public void setDecisionTableForIssue(long id, String location) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(id, location);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);
		decisionTableData = new HashMap<>();
		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType().equals(KnowledgeType.ALTERNATIVE) || elem.getType().equals(KnowledgeType.DECISION)) {
				decisionTableData.put(elem.getId(), new ArrayList<KnowledgeElement>());
				decisionTableData.get(elem.getId()).add(elem);
				getArguments(elem.getId(), decisionTableData.get(elem.getId()), location);
			}
		}
	}

	private void getArguments(long id, List<KnowledgeElement> arguments, String location) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(id, location);
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

	public Map<Long, List<KnowledgeElement>> getDecisionTableData() {
		return this.decisionTableData;
	}
}