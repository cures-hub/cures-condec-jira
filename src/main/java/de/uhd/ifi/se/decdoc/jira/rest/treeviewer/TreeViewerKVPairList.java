package de.uhd.ifi.se.decdoc.jira.rest.treeviewer;

import java.util.ArrayList;

import de.uhd.ifi.se.decdoc.jira.util.Pair;
/**
 * @author Ewald Rode
 * @description Utility class for access
 */
public class TreeViewerKVPairList {
	/*
     * For test use Only 
     */
    public void init() {
    	new TreeViewerKVPairList();
    	this.kvpList= new ArrayList<>();
    }
	public static ArrayList<Pair<String, String>> kvpList;
}
