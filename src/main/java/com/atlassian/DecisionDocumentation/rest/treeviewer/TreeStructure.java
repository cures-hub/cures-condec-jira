package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.DecisionDocumentation.util.Pair;
import com.atlassian.jira.issue.Issue;

/**
 * 
 * @author Ewald Rode
 * @description
 */
public class TreeStructure {
	
	@XmlElement
    public boolean multiple;
	
	@XmlElement
    public boolean check_callback;
	
	/*Design-Einstellungen*/
	@XmlElement
    public Themes themes;
	
	@XmlElement
    public HashSet<Data> data;
	
	public TreeStructure(List<Issue> issueList){
		this.multiple = false;
		this.check_callback = true;
		this.themes = new Themes();
		this.data = new HashSet<Data>();
		/*
		 * kvpList speichert die KeyValuePairs aller Parent-Child Beziehungen um nachvollziehen zu koennen, welche Knoten bereits in den Baum aufgenommen wurden,
		 * dies ist insbesondere noetig um Endlos-SChleifen vorzubeugen
		 */
		for (int index = 0; index < issueList.size(); ++index){
    		if(issueList.get(index).getIssueType().getName().equals("Decision")){
    			TreeViewerKVPList.kvpList = new ArrayList<Pair<String, String>>();
    			Pair<String,String> kvp = new Pair<String,String>("root",issueList.get(index).getKey());
    			TreeViewerKVPList.kvpList.add(kvp);
    			this.data.add(new Data(issueList.get(index)));
    		}
    	}
	}
}
