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
	
	/*
	 * Fills the HTML elements for basic filter criteria such as knowledge types, status, ... of a view.
	 *
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.fillFilterElements = function (viewIdentifier, selectedKnowledgeTypes) {
		conDecFiltering.initDropdown("status-dropdown-" + viewIdentifier, conDecAPI.knowledgeStatus);
        conDecFiltering.initDropdown("knowledge-type-dropdown-" + viewIdentifier, conDecAPI.getKnowledgeTypes(), selectedKnowledgeTypes);
    	
        conDecAPI.fillDecisionGroupSelect("select2-decision-group-" + viewIdentifier); // TODO Refactor and move method to conDecFiltering
    	conDecAPI.getLinkTypes(function (linkTypes) {
			var linkTypeArray = [];
			for (linkType in linkTypes) {
				if (linkType !== undefined) {
					linkTypeArray.push(linkType);
				}				
			}
			conDecFiltering.initDropdown("link-type-dropdown-" + viewIdentifier, linkTypeArray);
		});	
	};
	
	/*
	 * Reads the filter settings from the HTML elements of a view.
	 *
	 * external references: condec.jira.issue.module, condec.evolution.page, condec.relationship.page
	 */
	ConDecFiltering.prototype.getFilterSettings = function(viewIdentifier) {
		var filterSettings = {};
		
		// Read search term
		var searchInput = document.getElementById("search-input-" + viewIdentifier);		 
		if (searchInput !== null) {
			filterSettings["searchTerm"] = searchInput.value;
		}
		
		// Read selected knowledge types
		var types = conDecFiltering.getSelectedItems("knowledge-type-dropdown-" + viewIdentifier);
		filterSettings["knowledgeTypes"] = types;
		
		// Read selected status
		var status = conDecFiltering.getSelectedItems("status-dropdown-" + viewIdentifier);
		filterSettings["status"] = status;
		
		// Read selected groups
		var selectedGroups = conDecFiltering.getSelectedGroups("select2-decision-group-" + viewIdentifier);
		filterSettings["groups"] = selectedGroups;
		
		// Read selected link types
		var linkTypes = conDecFiltering.getSelectedItems("link-type-dropdown-" + viewIdentifier);
		filterSettings["linkTypes"] = linkTypes;
		
		// Read selected time frame
		var startDatePicker = document.getElementById("start-date-picker-" + viewIdentifier);
		var endDatePicker = document.getElementById("end-date-picker-" + viewIdentifier);
		if (startDatePicker !== null && endDatePicker !== null) {
			var startDate = new Date(startDatePicker.value).getTime();
			filterSettings["startDate"] = startDate;
			
			var endDate = new Date(endDatePicker.value).getTime();
			filterSettings["endDate"] = endDate;
		}
		
		// Read selected min and max degree (number of linked elements for a element/node)
		var minDegreeInput = document.getElementById("min-degree-input-" + viewIdentifier);
		var maxDegreeInput = document.getElementById("max-degree-input-" + viewIdentifier);
		if (minDegreeInput !== null && maxDegreeInput !== null) {
			filterSettings["minDegree"] = minDegreeInput.value;
			filterSettings["maxDegree"] = maxDegreeInput.value;
		}
		
		// Read whether only decision knowledge elements (issue, decision, alternative, arguments, ...) should be shown
		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input-" + viewIdentifier);
		if (isOnlyDecisionKnowledgeShownInput !== null) {
			filterSettings["isOnlyDecisionKnowledgeShown"] = isOnlyDecisionKnowledgeShownInput.checked;
		}
		
		// Read selected maximal link distance from selected element in knowledge graph
		var linkDistanceInput = document.getElementById("link-distance-input-" + viewIdentifier);
		if (linkDistanceInput !== null) {
			filterSettings["linkDistance"] = linkDistanceInput.value;
		}		

		// Read whether only incompletely documented elements should be shown
		if (status !== null) {
			var indexOfIncomplete = status.indexOf("incomplete");
			if (indexOfIncomplete !== -1) {
				filterSettings["isIncompleteKnowledgeShown"] = true;
			}
		}		
		
		// Read selected documentation locations, e.g. Jira issue comments + description, code, ...
		var documentationLocations = conDecFiltering.getSelectedItems("documentation-location-dropdown-" + viewIdentifier);
		filterSettings["documentationLocations"] = documentationLocations;

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
		if (dropdown === null || dropdown === undefined || dropdown.length === 0) {
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
		if (selectedGroupsObj === null || selectedGroupsObj === undefined) {
			return null;
		}
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
	
	/*
	 * external references: condec.jira.issue.module, condec.knowledge.page
	 */
	ConDecFiltering.prototype.fillDatePickers = function(viewIdentifier, deltaDays) {
		var startDate = new Date();
		startDate.setDate(startDate.getDate() - deltaDays);
		document.getElementById("start-date-picker-" + viewIdentifier).value = startDate.toISOString().substr(0, 10); 
		document.getElementById("end-date-picker-" + viewIdentifier).value = new Date().toISOString().substr(0, 10);
	};

	// export ConDecFiltering
	global.conDecFiltering = new ConDecFiltering();
})(window);