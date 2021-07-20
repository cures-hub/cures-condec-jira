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
(function(global) {

	var ConDecFiltering = function() {
		console.log("conDecFiltering constructor");
	};

	/**
	 * Fills the HTML elements for basic filter criteria such as knowledge
	 * types, status, ... of a view.
	 */
	ConDecFiltering.prototype.fillFilterElements = function(viewIdentifier, selectedKnowledgeTypes) {
		// dropdown menus
		this.fillDropdownMenus(viewIdentifier, selectedKnowledgeTypes);

		// selected element
		var jiraIssueKey = conDecAPI.getIssueKey();
		if (jiraIssueKey !== null && jiraIssueKey !== undefined && viewIdentifier !== "chronology") {
			document.getElementById("selected-element-" + viewIdentifier).innerText = jiraIssueKey;
		}

		// quality highlighting	
		this.fillMinimumCoverageAndMaximumLinkDistance(viewIdentifier, conDecAPI.getProjectKey())

		// change impact highlighting	
		conDecAPI.getChangeImpactAnalysisConfiguration(conDecAPI.getProjectKey(), (error, config) => {
			$("#decay-input-" + viewIdentifier)[0].value = config["decayValue"];
			$("#threshold-input-" + viewIdentifier)[0].value = config["threshold"];
			conDecFiltering.initDropdown("propagation-rule-dropdown-" + viewIdentifier,
				config["propagationRules"], []);
		});

		window.onbeforeunload = null;
	};

	/**
	 * Inits the filter button. NO instant filtering is done on change events.
	 */
	ConDecFiltering.prototype.addOnClickEventToFilterButton = function(viewIdentifier, callback) {
		var filterButton = document.getElementById("filter-button-" + viewIdentifier);
		addOnClickEventToButton(filterButton, viewIdentifier, callback);
	};

	/**
	 * Inits the change impact analysis button.
	 */
	ConDecFiltering.prototype.addOnClickEventToChangeImpactButton = function(viewIdentifier, callback) {
		var ciaButton = document.getElementById("cia-button-" + viewIdentifier);
		addOnClickEventToButton(ciaButton, viewIdentifier, (filterSettings) => {
			if (filterSettings["selectedElement"] === undefined) {
				conDecAPI.showFlag("error", "You need to select an element to perform change impact analysis!");
			} else {
				filterSettings["areChangeImpactsHighlighted"] = true;
				callback(filterSettings);
			}
		});
	};

	function addOnClickEventToButton(button, viewIdentifier, callback) {
		button.addEventListener("click", function(event) {
			var filterSettings = conDecFiltering.getFilterSettings(viewIdentifier);
			callback(filterSettings);
		});
	}

	/**
	 * Reads the filter settings from the HTML elements of a view.
	 *
	 * external references: condec.tree.viewer, condec.treant, condec.decision.table, 
	 * condec.knowledge.page, condec.rationale.backlog
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

		// Read selected min and max degree (number of linked elements for an element/node)
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
				filterSettings["isOnlyIncompleteKnowledgeShown"] = true;
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

		// Read whether filtered nodes should be replaced by links
		var createTransitiveLinksInput = document.getElementById("is-transitive-links-input-" + viewIdentifier);
		if (createTransitiveLinksInput !== null) {
			filterSettings["createTransitiveLinks"] = createTransitiveLinksInput.checked;
		}

		// Read whether test code should be shown
		var isTestCodeShownInput = document.getElementById("is-test-code-input-" + viewIdentifier);
		if (isTestCodeShownInput !== null) {
			filterSettings["isTestCodeShown"] = isTestCodeShownInput.checked;
		}

		// Read selected element
		var selectedElementOutput = document.getElementById("selected-element-" + viewIdentifier);
		if (selectedElementOutput !== null && selectedElementOutput.innerText !== "-") {
			filterSettings["selectedElement"] = selectedElementOutput.innerText;
		}

		// Read whether nodes that violate the definition of done (DoD) should be highlighted (colored)
		var isDoDViolationShownInput = document.getElementById("is-dod-violation-shown-input-" + viewIdentifier);
		if (isDoDViolationShownInput !== null) {
			filterSettings["areQualityProblemsHighlighted"] = isDoDViolationShownInput.checked;
		}

		// Read definition of done (DoD) (in particular decision coverage config)
		var minDecisionCoverageInput = document.getElementById("minimum-number-of-decisions-input-" + viewIdentifier);
		var maxLinkDistanceInput = document.getElementById("link-distance-to-decision-number-input-" + viewIdentifier);
		if (minDecisionCoverageInput !== null && maxLinkDistanceInput !== null) {
			filterSettings["definitionOfDone"] = {
				"minimumDecisionsWithinLinkDistance": minDecisionCoverageInput.value,
				"maximumLinkDistanceToDecisions": maxLinkDistanceInput.value
			}
		}

		filterSettings["changeImpactAnalysisConfig"] = {};
		// Read decay value for change impact analysis (CIA)
		var decayValue = document.getElementById("decay-input-" + viewIdentifier);
		if (decayValue !== null) {
			filterSettings["changeImpactAnalysisConfig"]["decayValue"] = decayValue.value;
		}

		// Read threshold value for change impact analysis (CIA)
		var threshold = document.getElementById("threshold-input-" + viewIdentifier);
		if (threshold !== null) {
			filterSettings["changeImpactAnalysisConfig"]["threshold"] = threshold.value;
		}

		// Read whether knowledge graph should be shown with CIA context or not
		var context = document.getElementById("context-input-" + viewIdentifier);
		if (context !== null) {
			filterSettings["changeImpactAnalysisConfig"]["context"] = context.value;
		}

		// Read propagation rules for change impact analysis (CIA)
		var propagationRules = conDecFiltering.getSelectedItems("propagation-rule-dropdown-" + viewIdentifier);
		if (propagationRules) {
			filterSettings["changeImpactAnalysisConfig"]["propagationRules"] = propagationRules;
		}

		return filterSettings;
	};

	/**
	 * external references: condec.dashboard
	 */
	ConDecFiltering.prototype.fillDropdownMenus = function (viewIdentifier, selectedKnowledgeTypes) {
		this.initDropdown("source-knowledge-type-dropdown-" + viewIdentifier, conDecAPI.getKnowledgeTypes(),
			selectedKnowledgeTypes, ["Other", "Code"]);
		this.initDropdown("knowledge-type-dropdown-" + viewIdentifier, conDecAPI.getKnowledgeTypes(),
			selectedKnowledgeTypes, ["Other", "Code"]);
		this.initDropdown("status-dropdown-" + viewIdentifier, conDecAPI.knowledgeStatus);
		this.initDropdown("documentation-location-dropdown-" + viewIdentifier, conDecAPI.documentationLocations);
		this.initDropdown("link-type-dropdown-" + viewIdentifier, conDecAPI.getLinkTypes());
		this.initDropdown("decision-group-dropdown-" + viewIdentifier, conDecAPI.getAllDecisionGroups(), []);
		this.fillDecisionGroupSelect("select2-decision-group-" + viewIdentifier, conDecAPI.getAllDecisionGroups());
	}

	/**
	 * external references: condec.rationale.backlog, condec.decision.table
	 * condec.dashboard
	 */
	ConDecFiltering.prototype.initDropdown = function(dropdownId, items, selectedItems, unselectedItems) {
		var dropdown = document.getElementById(dropdownId);
		if (dropdown === null || dropdown === undefined || dropdown.length === 0) {
			return null;
		}
		dropdown.innerHTML = "";
		if (items !== undefined && items !== null && items.length > 0) {
			for (var index = 0; index < items.length; index++) {
				var isSelected = "";
				isSelected = "checked";
				if (selectedItems !== undefined && selectedItems !== null) {
					if (!selectedItems.includes(items[index])) {
						isSelected = "";
					}
				}
				if (unselectedItems !== undefined && selectedItems !== null) {
					if (unselectedItems.includes(items[index])) {
						isSelected = "";
					}
				}
				dropdown.insertAdjacentHTML("beforeend", "<aui-item-checkbox interactive " + isSelected + ">"
					+ items[index] + "</aui-item-checkbox>");
			}
		}
		return dropdown;
	};

	/**
	 * external references: condec.dashboard
	 */
	ConDecFiltering.prototype.getSelectedItems = function(dropdownId) {
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

	/**
	 * external references: none, only used locally in condec.filtering
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

	/**
	 * Sets default values to the date pickers for creation/update dates of an element 
	 * so that only the recently created/updated elements are shown.
	 * 
	 * external references: condec.rationale.backlog, condec.matrix, ...
	 */
	ConDecFiltering.prototype.fillDatePickers = function(viewIdentifier, deltaDays) {
		var startDate = new Date();
		startDate.setDate(startDate.getDate() - deltaDays);
		document.getElementById("start-date-picker-" + viewIdentifier).value = startDate.toISOString().substr(0, 10);
		document.getElementById("end-date-picker-" + viewIdentifier).value = new Date().toISOString().substr(0, 10);
	};

	/**
	 * Fills the filter for decision groups/levels.
	 */
	ConDecFiltering.prototype.fillDecisionGroupSelect = function(elementId, groups) {
		var selectGroupField = document.getElementById(elementId);
		if (selectGroupField === null || selectGroupField === undefined) {
			return null;
		}
		selectGroupField.innerHTML = "";
		if (groups !== undefined && groups !== null && groups.length > 0) {
			for (var i = 0; i < groups.length; i++) {
				if (groups[i] !== "High_Level" && groups[i] !== "Medium_Level" && groups[i] !== "Realization_Level") {
					selectGroupField.insertAdjacentHTML("beforeend", "<option value='" + groups[i] + "'>" + groups[i] + "</option>");
				}
			}
		}
		AJS.$("#" + elementId).auiSelect2();
	};

	/**
	 * Fills the filter for the minimum decision coverage and
	 * the maximum link distance from the definition of done.
	 *
	 * external references: condec.dashboard
	 */
	ConDecFiltering.prototype.fillMinimumCoverageAndMaximumLinkDistance = function(viewIdentifier, projectKey) {
		conDecDoDCheckingAPI.getDefinitionOfDone(projectKey, (definitionOfDone) => {
			var minDecisionCoverageInput = document.getElementById("minimum-number-of-decisions-input-" + viewIdentifier);
			var maxLinkDistanceInput = document.getElementById("link-distance-to-decision-number-input-" + viewIdentifier);
			if (minDecisionCoverageInput !== null && maxLinkDistanceInput !== null) {
				minDecisionCoverageInput.value = definitionOfDone.minimumDecisionsWithinLinkDistance;
				maxLinkDistanceInput.value = definitionOfDone.maximumLinkDistanceToDecisions;
			}
		});
	};

	global.conDecFiltering = new ConDecFiltering();
})(window);