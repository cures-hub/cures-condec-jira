/**
 * This module implements the solution option recommendation for decision problems (=decision guidance).
 * The recommended solution options are taken from external knowledge sources, such as 
 * other Jira projects or DBPedia.
 */
(function(global) {

	let ConDecDecisionGuidance = function() {

	};

	ConDecDecisionGuidance.prototype.initView = function() {
		// get all the elements for the dropdown and fill the dropdown
		const filterSettings = {
			knowledgeTypes: ["Issue"],
			selectedElement: conDecAPI.getIssueKey(),
		};
		conDecAPI.getKnowledgeElements(filterSettings, (results) => {
			const filteredResults = results.filter((element) => element.id !== JIRA.Issue.getIssueId())
			console.log(filteredResults)
			fillDecisionProblemDropDown(filteredResults)
		});
		// add button listener
		this.addOnClickListenerForRecommendations();
		// this.issueSelected()
	};

	ConDecDecisionGuidance.prototype.updateView = function() {
	};

	ConDecDecisionGuidance.prototype.issueSelected = function(currentIssue) {
		conDecDecisionGuidanceAPI.getRecommendations(conDecAPI.getProjectKey(), conDecAPI.getIssueKey())
			.then((recommendations, error) => {
				if (recommendations.length > 0 && error === undefined) {
					buildQuickRecommendationTable(recommendations, currentIssue);
				}
			});
	};

	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function() {
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			let dropDownElement = document.getElementById("decision-guidance-dropdown-items");
			let currentIssue = dropDownElement.options[dropDownElement.options.selectedIndex];
			$("#recommendation-container tbody tr").remove();
			const spinner = $("#loading-spinner-recommendation");
			spinner.show();
			conDecDecisionGuidanceAPI.getRecommendations(conDecAPI.getProjectKey(), conDecAPI.getIssueKey())
				.then((recommendations, error) => {
					if ( error === null || error === undefined) {
						if (Object.keys(recommendations).length > 0){
							buildRecommendationTable(recommendations[currentIssue.id], currentIssue);
						} else {
							document.getElementById("recommendation-container-table-body").innerHTML = "<i>No recommendations found!</i>";
						}
					}
					spinner.hide();
				});
		});
	};

	function buildRecommendationTable(recommendations, currentIssue) {
		const table = $("#recommendation-container tbody");
		let counter = 0;
		recommendations.forEach(recommendation => {
			counter++;
			let tableRow = "<tr>";
			tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.summary + "</a></td>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSource.name + "<span class='aui-icon aui-icon-small " + recommendation.knowledgeSource.icon + "'>Knowledge Source Type</span></div></td>";
			tableRow += "<td>" + conDecRecommendation.buildScore(recommendation.score, "score_" + counter) + "</td>";
			tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>" + "Accept" + "</button></td>";
			tableRow += "<td><ul>";
			recommendation.arguments.forEach(argument => {
				if (argument) {
					tableRow += "<li><img src='" + argument.image + "'/>";
					tableRow += argument.summary + "</li>";
				}
			});
			tableRow += "</ul></td>";
			tableRow += "</tr>";
			table.append(tableRow);

			$("#row_" + counter).click(function() {
				onAcceptClicked(recommendation, currentIssue);
			});
		});
		conDecAPI.showFlag("success", "#Recommendations: " + counter);
	}

	function buildQuickRecommendationTable(recommendations, currentIssue) {
		document.getElementById("decision-problem-summary").innerText = currentIssue.summary;
		let counter = 0;
		var columns = "";
		var topResults = recommendations[currentIssue.id].slice(0, 4);
		topResults.forEach(recommendation => {
			counter++;
			let tableRow = "<tr>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'><span class='aui-icon aui-icon-small " + recommendation.knowledgeSource.icon + "'>Knowledge Source Type</span><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.summary + "</a></div></td>";
			tableRow += "<td>" + conDecRecommendation.buildScore(recommendation.score, "score_quick" + counter) + "</td>";
			tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_quick_" + counter + "' class='aui-button-link'>Accept</button></td>";
			tableRow += "</tr>";
			columns += tableRow;
		});

		document.getElementById("quick-recommendations-table-body").innerHTML = columns;

		AJS.flag({
			body: document.getElementById("quick-recommendations").outerHTML,
			title: "Recommendations for Decision Problem"
		});

		var i = 0;
		topResults.forEach(recommendation => {
			i++;
			$("#row_quick_" + i).click(function() {
				onAcceptClicked(recommendation, currentIssue);
			});
		});

		$("#more-recommendations").click(function(event) {
			$("#recommendation-container tbody tr").remove();
			buildRecommendationTable(recommendations, currentIssue);
		});
	}

	function onAcceptClicked(recommendation, currentIssue) {
		conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.summary, "", function (id, documentationLocation) {
			recommendation.arguments.forEach(argument => {
				conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function () {
					conDecAPI.showFlag("success", "Recommendation was added successfully!");
				});
			});
		});
	}

	/**
	 * @param issues
	 *            all decision problems found.
	 */
	function fillDecisionProblemDropDown(issues) {
		let dropDown = document.getElementById("decision-guidance-dropdown-items");
		dropDown.innerHTML = "";

		if (!issues.length) {
			dropDown.innerHTML += "<option disabled>Could not find any issue. Please create a new issue!</option>";
			return;
		}

		for (let issue of issues) {
			dropDown.innerHTML += "<option value='" + issue.id + "'>" + issue.summary + "</option>";
		}


	}
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);

