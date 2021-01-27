/**
 * This module is responsible for filling the filter HTML elements and for
 * filtering functionality.
 * 
 * Requires no other module
 * 
 * Is required by: conDecJiraIssueModule, conDecEvolutionPage,
 * ConDecRelationshipPage, conDecMatrix, conDecRationaleBacklog,
 * conDecKnowledgePage
 * 
 * @issue Should filters change all views or only the current view?
 * @decision Filters are only applied in the current view using updateView()!
 * @alternative We could update all views using conDecObservable.notify()!
 * @pro The user could reuse the filter settings, which is more useable.
 * @con This would need more computation and decreases performance.
 * 
 * @issue Should filtering be performed instantly after every change or using a
 *        "filter" button?
 * @decision Perform filtering instantly after changes in some views, e.g.
 *           conDecRationaleBacklog! Perform filtering using a "filter" button
 *           in other views, e.g. chronology view!
 */
(function (global) {

	var ConDecFiltering = function () {
		console.log("conDecFiltering constructor");
	};

	/*
	 * Fills the HTML elements for basic filter criteria such as knowledge
	 * types, status, ... of a view.
	 * 
	 * external references: condec.jira.issue.module, condec.evolution.page,
	 * condec.relationship.page, condec.matrix
	 */
	ConDecFiltering.prototype.fillFilterElements = function (viewIdentifier, selectedKnowledgeTypes) {
		this.initDropdown("status-dropdown-" + viewIdentifier, conDecAPI.knowledgeStatus);
		this.initDropdown("knowledge-type-dropdown-" + viewIdentifier, conDecAPI.getKnowledgeTypes(),
			selectedKnowledgeTypes, ["Other", "Code"]);
		this.initDropdown("link-type-dropdown-" + viewIdentifier, conDecAPI.getLinkTypes());
		this.fillDecisionGroupSelect("select2-decision-group-" + viewIdentifier);
		this.initDropdown("documentation-location-dropdown-" + viewIdentifier, conDecAPI.documentationLocations);
	};

	/**
	 * For views with filter button, i.e., NO instant filtering.
	 * 
	 * external references: condec.jira.issue.module, condec.evolution.page,
	 * condec.relationship.page, condec.matrix
	 */
	ConDecFiltering.prototype.addOnClickEventToFilterButton = function (viewIdentifier, callback) {
		var filterButton = document.getElementById("filter-button-" + viewIdentifier);

		filterButton.addEventListener("click", function (event) {
			var filterSettings = conDecFiltering.getFilterSettings(viewIdentifier);
			callback(filterSettings);
		});
	};

	/**
	 * For views without filter button but instant filtering.
	 * 
	 * external references: condec.jira.issue.module, condec.knowledge.page,
	 * condec.rationale.backlog
	 */
	ConDecFiltering.prototype.addOnChangeEventToFilterElements = function (viewIdentifier, callback, isSearchInputEvent = true) {
		$("#select2-decision-group-" + viewIdentifier).on("change.select2",
			function () {
				callback();
			});

		var knowledgeTypeDropdown = document.getElementById("knowledge-type-dropdown-" + viewIdentifier);
		if (knowledgeTypeDropdown !== null) {
			knowledgeTypeDropdown.addEventListener("click", () => callback());
		}

		var statusDropdown = document.getElementById("status-dropdown-" + viewIdentifier);
		if (statusDropdown !== null) {
			statusDropdown.addEventListener("click", () => callback());
		}

		var documentationLocationDropdown = document.getElementById("documentation-location-dropdown-" + viewIdentifier);
		if (documentationLocationDropdown !== null) {
			documentationLocationDropdown.addEventListener("click", () => callback());
		}

		var filterElements = [];

		var searchInput = document.getElementById("search-input-" + viewIdentifier);
		if (isSearchInputEvent && searchInput !== null) {
			filterElements.push(searchInput);
		}

		var linkDistanceInput = document.getElementById("link-distance-input-" + viewIdentifier);
		if (linkDistanceInput !== null) {
			filterElements.push(linkDistanceInput);
		}

		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input-" + viewIdentifier);
		if (isOnlyDecisionKnowledgeShownInput !== null) {
			filterElements.push(isOnlyDecisionKnowledgeShownInput);
		}

		var minLinkNumberInput = document.getElementById("min-degree-input-" + viewIdentifier);
		if (minLinkNumberInput !== null) {
			filterElements.push(minLinkNumberInput);
		}

		var maxLinkNumberInput = document.getElementById("max-degree-input-" + viewIdentifier);
		if (maxLinkNumberInput !== null) {
			filterElements.push(maxLinkNumberInput);
		}

		var endDatePicker = document.getElementById("end-date-picker-" + viewIdentifier);
		if (endDatePicker !== null) {
			filterElements.push(endDatePicker);
		}

		var startDatePicker = document.getElementById("start-date-picker-" + viewIdentifier);
		if (startDatePicker !== null) {
			filterElements.push(startDatePicker);
		}

		var isIrrelevantTextShownInput = document.getElementById("show-irrelevant-text-input-" + viewIdentifier);
		if (isIrrelevantTextShownInput !== null) {
			filterElements.push(isIrrelevantTextShownInput);
        }
        
        var isTransitiveLinksInput = document.getElementById("is-transitive-links-input-" + viewIdentifier);
		if (isTransitiveLinksInput !== null) {
			filterElements.push(isTransitiveLinksInput);
		}

		filterElements.forEach(function (filterElement) {
			filterElement.addEventListener("input", () => callback());
		});
	};

	/*
	 * Reads the filter settings from the HTML elements of a view.
	 * 
	 * external references: condec.jira.issue.module, condec.knowledge.page,
	 * condec.evolution.page, condec.rationale.backlog
	 */
	ConDecFiltering.prototype.getFilterSettings = function (viewIdentifier) {
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

		// Read selected min and max degree (number of linked elements for a
		// element/node)
		var minDegreeInput = document.getElementById("min-degree-input-" + viewIdentifier);
		var maxDegreeInput = document.getElementById("max-degree-input-" + viewIdentifier);
		if (minDegreeInput !== null && maxDegreeInput !== null) {
			filterSettings["minDegree"] = minDegreeInput.value;
			filterSettings["maxDegree"] = maxDegreeInput.value;
		}

		// Read whether only decision knowledge elements (issue, decision,
		// alternative, arguments, ...) should be shown
		var isOnlyDecisionKnowledgeShownInput = document.getElementById("is-decision-knowledge-only-input-"
			+ viewIdentifier);
		if (isOnlyDecisionKnowledgeShownInput !== null) {
			filterSettings["isOnlyDecisionKnowledgeShown"] = isOnlyDecisionKnowledgeShownInput.checked;
		}

		// Read selected maximal link distance from selected element in
		// knowledge graph
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

		// Read selected documentation locations, e.g. Jira issue comments +
		// description, code, ...
		var documentationLocations = conDecFiltering.getSelectedItems("documentation-location-dropdown-"
			+ viewIdentifier);
		filterSettings["documentationLocations"] = documentationLocations;

		// Read whether sentences that are not classified as decision knowledge elements should
		// be included in the filtered knowledge graph
		var isIrrelevantTextShownInput = document.getElementById("show-irrelevant-text-input-" + viewIdentifier);
		if (isIrrelevantTextShownInput !== null) {
			filterSettings["isIrrelevantTextShown"] = isIrrelevantTextShownInput.checked;
		}

		// Read whether knowledge graph should be shown with hierarchy of nodes/knowledge elements
		var isHierarchicalGraphInput = document.getElementById("is-hierarchical-input-" + viewIdentifier);
		if (isHierarchicalGraphInput !== null) {
			filterSettings["isHierarchical"] = isHierarchicalGraphInput.checked;
		}

        // Read whether knowledge graph should be shown with transitive links
        var createTransitiveLinksInput = document.getElementById("is-transitive-links-input-" + viewIdentifier);
        if (createTransitiveLinksInput !== null) {
            filterSettings["createTransitiveLinks"] = createTransitiveLinksInput.checked;
        }

		return filterSettings;
	};

	/*
	 * external references: condec.knowledge.page, condec.rationale.backlog
	 */
	ConDecFiltering.prototype.initDropdown = function (dropdownId, items, selectedItems, unselectedItems) {
		var dropdown = document.getElementById(dropdownId);
		if (dropdown === null || dropdown === undefined || dropdown.length === 0) {
			return null;
		}
		dropdown.innerHTML = "";
		for (var index = 0; index < items.length; index++) {
			var isSelected = "checked";
			if (selectedItems !== undefined) {
				if (!selectedItems.includes(items[index])) {
					isSelected = "";
				}
			}
			if (unselectedItems !== undefined) {
				if (unselectedItems.includes(items[index])) {
					isSelected = "";
				}
			}
			dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + isSelected + ">"
				+ items[index] + "</aui-item-checkbox>");
		}
		return dropdown;
	};

	/*
	 * external references: none, only used locally in condec.filtering
	 */
	ConDecFiltering.prototype.getSelectedItems = function (dropdownId) {
		var dropdown = document.getElementById(dropdownId);
		if (dropdown === null || dropdown === undefined || dropdown.length === 0) {
			return null;
		}
		var selectedItems = [];
		if (dropdown.children.length === 1) {
			/*
			 * @issue In some cases a <div role="application"> is added for
			 * unknown reasons. How to prevent this? How to make reading checked
			 * items more deterministic?
			 */
			dropdown = dropdown.children[0];
		}
		for (var i = 0; i < dropdown.children.length; i++) {
			var option = dropdown.children[i];
			if (option.hasAttribute("checked")) {
				selectedItems.push(option.textContent);
			}
		}
		return selectedItems;
	};

	/*
	 * external references: condec.knowledge.page, condec.evolution.page,
	 * condec.relationship.page, condec.rationale.backlog
	 */
	ConDecFiltering.prototype.getSelectedGroups = function (selectId) {
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
	ConDecFiltering.prototype.fillDatePickers = function (viewIdentifier, deltaDays) {
		var startDate = new Date();
		startDate.setDate(startDate.getDate() - deltaDays);
		document.getElementById("start-date-picker-" + viewIdentifier).value = startDate.toISOString().substr(0, 10);
		document.getElementById("end-date-picker-" + viewIdentifier).value = new Date().toISOString().substr(0, 10);
	};

	ConDecFiltering.prototype.fillDecisionGroupSelect = function (elementId) {
		var selectGroupField = document.getElementById(elementId);
		if (selectGroupField === null || selectGroupField === undefined) {
			return null;
		}
		groups = conDecAPI.getAllDecisionGroups();
		if (groups !== null && groups.length > 0) {
			for (var i = 0; i < groups.length; i++) {
				if (groups[i] !== "High_Level" && groups[i] !== "Medium_Level" && groups[i] !== "Realization_Level") {
					selectGroupField.insertAdjacentHTML("beforeend", "<option value='" + groups[i] + "'>" + groups[i] + "</option>");
				}
			}
		}
		AJS.$("#" + elementId).auiSelect2();
	};

	global.conDecFiltering = new ConDecFiltering();
})(window);