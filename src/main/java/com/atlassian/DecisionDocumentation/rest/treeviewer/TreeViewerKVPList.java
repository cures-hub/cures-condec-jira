package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.List;

import com.atlassian.DecisionDocumentation.util.Pair;

/**
 * 
 * @author Ewald Rode
 * @description Utility-Klasse die beim Abbruch der Rekursion genutzt wird, sofern ein KeyValuePair bereits in der Liste vorliegt
 */
public class TreeViewerKVPList {
	public static List<Pair<String, String>> kvpList;
}
