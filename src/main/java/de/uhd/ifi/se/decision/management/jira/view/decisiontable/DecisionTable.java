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

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

import org.jgrapht.traverse.DepthFirstIterator;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

@XmlRootElement(name = "decisiontable")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecisionTable {

	private KnowledgeGraph graph;
	private KnowledgePersistenceManager persistenceManager;

	@XmlElement
	private List<KnowledgeElement> issues;

	@XmlElement
	private Map<Long, List<KnowledgeElement>> decisionTableData;

	@XmlElement
	private Map<String, Map<Long, List<KnowledgeElement>>> tmpDecisionTableData;

	public DecisionTable(String projectKey) {
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
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

	private Map<Long, List<KnowledgeElement>> getCriteria(ApplicationUser user, Map<Long, List<KnowledgeElement>> map) {
		String query = "?jql=project=" + persistenceManager.getProjectKey() + " AND type=\"Non-Functional Requirement\"";
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, persistenceManager.getProjectKey(), query);
		for(Issue i : queryHandler.getJiraIssuesFromQuery()) {
			map.put(i.getId(), new ArrayList<KnowledgeElement>());
			map.get(i.getId()).add(new KnowledgeElement(i));
		}
		return map;
	}
	
	public void setDecisionTableForIssue(long id, String location, ApplicationUser user) {
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(id, location);
		Set<Link> outgoingLinks = this.graph.outgoingEdgesOf(rootElement);
		decisionTableData = new HashMap<>();
		tmpDecisionTableData = new HashMap<>();
		tmpDecisionTableData.put("alternatives", new HashMap<>());
		tmpDecisionTableData.put("criteria", new HashMap<>());
		for (Link currentLink : outgoingLinks) {
			KnowledgeElement elem = currentLink.getTarget();
			if (elem.getType().equals(KnowledgeType.ALTERNATIVE) || elem.getType().equals(KnowledgeType.DECISION)) {
				decisionTableData.put(elem.getId(), new ArrayList<KnowledgeElement>());
				tmpDecisionTableData.get("alternatives").put(elem.getId(), new ArrayList<KnowledgeElement>());
				decisionTableData.get(elem.getId()).add(elem);
				tmpDecisionTableData.get("alternatives").get(elem.getId()).add(elem);
				getArguments(elem.getId(), tmpDecisionTableData.get("alternatives").get(elem.getId()), location);
			}
		}
		getCriteria(user, tmpDecisionTableData.get("criteria"));
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

	public Map<String, Map<Long, List<KnowledgeElement>>> getDecisionTableData() {
		return this.tmpDecisionTableData;
	}
}