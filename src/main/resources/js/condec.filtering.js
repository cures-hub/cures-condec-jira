/*
 This module is responsible for filling the filter HTML elements and for filtering functionality.
 
 Requires no other module

 Is required by
 * conDecJiraIssueModule
 * conDecEvolutionPage
 * ConDecRelationshipPage
 */
(function(global) {

	var ConDecFiltering = function() {
		console.log("conDecFiltering constructor");
	};
	
	/**
	 * Reads the filter settings from the HTML elements of a view.
	 *
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.getFilterSettings = function(viewIdentifier) {
		var filterSettings = {};
		var searchInput = document.getElementById("search-input-" + viewIdentifier);		 
		if (searchInput !== undefined) {
			filterSettings["searchTerm"] = searchInput.value;
		}
		
		var types = conDecFiltering.getSelectedItems("knowledge-type-dropdown-" + viewIdentifier);
		filterSettings["knowledgeTypes"] = types;
		
		var status = conDecFiltering.getSelectedItems("status-dropdown-" + viewIdentifier);
		filterSettings["status"] = status;
		
		var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group-" + viewIdentifier);
		filterSettings["groups"] = selectedGroups;
		
		var linkTypes = conDecFiltering.getSelectedItems("linktype-dropdown");
		filterSettings["linkTypes"] = linkTypes;
		
		return filterSettings;
	};


	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.initDropdown = function(dropdownId, items, selectedItems) {
		var dropdown = document.getElementById(dropdownId);
		dropdown.innerHTML = "";
		for (var index = 0; index < items.length; index++) {
			var isSelected = "checked";
			if (selectedItems !== undefined) {
				if (!selectedItems.includes(items[index])) {
					isSelected = "";
				}
			}			
			dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + isSelected + ">"
			        + items[index] + "</aui-item-checkbox>");
		}
		return dropdown;
	};

	/*
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.getSelectedItems = function(dropdownId) {
		var dropdown = AJS.$("#" + dropdownId);
		if (dropdown === undefined) {
			return null;
		}
		var selectedItems = [];
		for (var i = 0; i < dropdown.children().size(); i++) {
			if (typeof dropdown.children().eq(i).attr("checked") !== typeof undefined
			        && dropdown.children().eq(i).attr("checked") !== false) {
				selectedItems.push(dropdown.children().eq(i).text());
			}
		}
		return selectedItems;
	};

	/*
	 * external references: condec.knowledge.page, condec.evolution.page, condec.relationship.page, 
	 * condec.rationale.backlog
	 */
	ConDecFiltering.prototype.getSelectedGroups = function(selectId) {
		var selectedGroupsObj = AJS.$("#" + selectId).select2("data");
		var selectedGroups = [];
		for (var i = 0; i <= selectedGroupsObj.length; i++) {
			if (selectedGroupsObj[i]) {
				selectedGroups[i] = selectedGroupsObj[i].text;
			}
		}
		return selectedGroups;
	};

	/*
	 * external references: condec.jira.issue.module, condec.knowledge.page
	 */
	ConDecFiltering.prototype.addEventListenerToLinkDistanceInput = function(inputId, callback) {
		var linkDistanceInput = document.getElementById(inputId);
		linkDistanceInput.addEventListener("input", function() {
			if (this.value >= 0) {
				callback();
			}
		});
	};

	// export ConDecFiltering
	global.conDecFiltering = new ConDecFiltering();
})(window);