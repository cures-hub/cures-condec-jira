package de.uhd.ifi.se.decision.management.jira.filtering;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "issueTypesForDropdown")
@XmlAccessorType(XmlAccessType.FIELD)
public class IssueTypesForDropdown {

	private List<String> allIssueTypes;
	private List<String> issueTypesMatchingFilter;
	@XmlElement
	private List<DropdownEntry> items;

	public IssueTypesForDropdown() {

	}

	public IssueTypesForDropdown(String projectKey, String query, ApplicationUser user) {
		GraphFiltering filter = new GraphFiltering(projectKey,query,user,false);
		filter.produceResultsFromQuery();
		this.allIssueTypes = new ArrayList<>();
		for (IssueType issueType : ComponentAccessor.getConstantsManager().getAllIssueTypeObjects()) {
			this.allIssueTypes.add(issueType.getName());
		}
		if (!filter.getIssueTypesInQuery().isEmpty()) {
			this.issueTypesMatchingFilter = filter.getIssueTypesInQuery();
		} else {
			this.issueTypesMatchingFilter = allIssueTypes;
		}
		for (String issueType : allIssueTypes) {
			DropdownEntry dropdownEntry = new DropdownEntry();
			dropdownEntry.setContent(issueType);
			dropdownEntry.setChecked(issueTypesMatchingFilter.contains(issueType));
			items.add(dropdownEntry);

		}

	}

	public List<DropdownEntry> getItems() {
		return items;
	}

	public void setItems(List<DropdownEntry> items) {
		this.items = items;
	}

	private class DropdownEntry {
		private String type;
		private boolean interactive;
		private boolean checked;
		private String content;

		public DropdownEntry() {
			this.type = "checkbox";
			this.interactive = true;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

}