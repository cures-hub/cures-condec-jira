/**
 * This module implements the solution option recommendation for decision problems.
 */
(function(global) {

	var recommendations;
	var globalCounter;
	var idOfExistingElement;
	var documentationLocationOfExistingElement;

	let ConDecDecisionGuidance = function() {
		this.globalCounter = 0;
		this.recommendations = []
		this.idOfExistingElement = 0;
		this.documentationLocationOfExistingElement = "s";
	};

	ConDecDecisionGuidance.prototype.initView = function() {
		conDecObservable.subscribe(this);
	};

	ConDecDecisionGuidance.prototype.updateView = function() {
		conDecDecisionTable.updateView();
	};

	ConDecDecisionGuidance.prototype.issueSelected = function(currentIssue) {
		const keyword = $("#recommendation-keyword");
		$("#recommendation-error").hide();
		conDecDecisionGuidanceAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), currentIssue.id, currentIssue.documentationLocation,
			function(recommendations, error) {
				if (error === null && recommendations.length > 0) {
					buildQuickRecommendationTable(recommendations, currentIssue);
				}
			});
	};

	/*
	 * external usage: condec.decision.table
	 */
	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function() {
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			const currentIssue = conDecDecisionTable.getCurrentIssue();
			$(this).prop("disabled", true);
			$("#recommendation-container tbody tr").remove();
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");
			spinner.show();
			$("#recommendation-error").hide();
			conDecDecisionGuidanceAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), currentIssue.id,
				currentIssue.documentationLocation, function(results, error) {
					if (error === null) {
						buildRecommendationTable(results);
					}
					$("#recommendation-button").prop("disabled", false);
					spinner.hide();
				});
		});
	};

	function buildRecommendationTable(results) {
		conDecDecisionGuidance.recommendations = results;
		const table = $("#recommendation-container tbody");

		let counter = 0;
		var sortedByScore = results.slice(0);
		sortedByScore.sort(function(a, b) {
			return b.score.totalScore - a.score.totalScore;
		});

		sortedByScore.forEach((recommendation) => {
			const localCounter = counter;
			counter += 1;
			let tableRow = "";

			tableRow += "<tr>";
			tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.recommendation + "</a></td>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSourceName + "<span class='aui-icon aui-icon-small " + recommendation.icon + "'>Knowledge Source Type</span></div></td>";
			tableRow += "<td>" + buildScore(recommendation.score, "score_" + counter) + "</td>";
			tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>" + "Accept" + "</button></td>";
			tableRow += "<td><ul>";
			recommendation.arguments.forEach((argument) => {
				if (argument) {
					tableRow += "<li><img src='" + argument.image + "'/>";
					tableRow += argument.summary + "</li>";
				}
			});
			tableRow += "</ul></td>";
			tableRow += "</tr>";
			table.append(tableRow);

			$(" #row_" + counter).click(function() {
				conDecDecisionGuidance.globalCounter = localCounter;
				const currentIssue = conDecDecisionTable.getCurrentIssue();
				conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.recommendation, "", function(id, documentationLocation) {
					conDecDecisionGuidance.idOfExistingElement = id;
					conDecDecisionGuidance.documentationLocationOfExistingElement = documentationLocation;

					recommendation.arguments.forEach(argument => {
						conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function() {
							conDecAPI.showFlag("success", "Recommendation was added successfully!");
						});
					});
				});
			});
		});
		conDecAPI.showFlag("success", "Results: " + counter);
		//Since the data is added later, the table must be set to sortable afterwards
		AJS.tablessortable.setTableSortable(AJS.$("#recommendation-container"));
	}

	function buildQuickRecommendationTable(results, currentIssue) {
		conDecDecisionGuidance.recommendations = results;
		document.getElementById("decision-problem-summary").innerText = currentIssue.summary;

		let counter = 0;
		var sortedByScore = results.slice(0);
		sortedByScore.sort(function(a, b) {
			return b.score.totalScore - a.score.totalScore;
		});
		
		var columns = "";

		var topResults = sortedByScore.slice(0, 4);
		topResults.forEach(recommendation => {
			counter += 1;
			let tableRow = "<tr>";
			tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'><span class='aui-icon aui-icon-small " + recommendation.icon + "'>Knowledge Source Type</span><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.recommendation + "</a></div></td>";
			tableRow += "<td>" + buildScore(recommendation.score, "score_quick" + counter) + "</td>";
			tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_quick_" + counter + "' class='aui-button-link'>Accept</button></td>";
			tableRow += "</tr>";
			columns += tableRow;
		});
		
		document.getElementById("quick-recommendations-table-body").innerHTML = columns;

		AJS.flag({
			type: "info",
			body: document.getElementById("quick-recommendations").outerHTML,
			title: "Quick Recommendation"
		});

		var i = 1;
		topResults.forEach(recommendation => {
			$("#row_quick_" + i).click(function() {
				conDecDecisionGuidance.globalCounter = i;
				const currentIssue = conDecDecisionTable.getCurrentIssue();
				conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative", recommendation.recommendation, "", function(id, documentationLocation) {
					conDecDecisionGuidance.idOfExistingElement = id;
					conDecDecisionGuidance.documentationLocationOfExistingElement = documentationLocation;

					recommendation.arguments.forEach(argument => {
						conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function() {
							conDecAPI.showFlag("success", "Recommendation was added successfully!");
						});
					});
				});
			});

			i = i + 1;
		});

		$("#more-recommendations").click(function(event) {
			const currentIssue = conDecDecisionTable.getCurrentIssue();
			$(this).prop("disabled", true);
			$("#recommendation-container tbody tr").remove() //TODO the rows are kept in the cache, but they should be removed completly
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");
			spinner.show();
			$("#recommendation-error").hide();
			conDecDecisionGuidanceAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), currentIssue.id, currentIssue.documentationLocation, function(results, error) {
				if (error === null) {
					buildRecommendationTable(results);
				}
				$("#recommendation-button").prop("disabled", false);
				spinner.hide();

			});
		});

		//Since the data is added later, the table must be set to sortable afterwards
		AJS.tablessortable.setTableSortable(AJS.$("#recommendation-container"));
	}

	function buildScore(scoreObject, ID) {
		var scoreControl = "<a data-aui-trigger aria-controls='score-explanation-" + ID + "'>" +
			+ scoreObject.totalScore.toFixed(2) + "%" +
			"</a>";

		var columns = "";		
		scoreObject.partScores.forEach(partScore => {
			columns += "<tr><td>" + partScore.explanation + "</td><td>" + partScore.totalScore.toFixed(2) + "</td></tr>";
		})
		document.getElementById("score-explanation-table-body").innerHTML = columns;
		return scoreControl + document.getElementById("score-explanation").outerHTML.replace("score-explanation", "score-explanation-" + ID);
	}

	global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);