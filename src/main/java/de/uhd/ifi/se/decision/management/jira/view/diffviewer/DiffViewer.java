package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jgit.lib.Ref;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Creates diff viewer content for a list of git repository branches
 */
@XmlRootElement(name = "DiffViewer")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiffViewer {

	@XmlElement
	private List<BranchDiff> branches;


	public DiffViewer(Map<Ref, List<KnowledgeElement>> ratBranchList) {
		branches = new ArrayList<>();
		if (ratBranchList == null) {
			return;
		}

		Iterator<Map.Entry<Ref, List<KnowledgeElement>>> it = ratBranchList.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Ref, List<KnowledgeElement>> entry = it.next();
			branches.add(new BranchDiff(entry.getKey().getName(), entry.getValue()));
		}
	}

	public List<BranchDiff> getBranches() {
		return branches;
	}
}
