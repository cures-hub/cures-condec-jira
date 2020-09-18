(function (global) {

	let ConDecDecisionGuidance = function ConDecDecisionGuidance() {};

	ConDecDecisionGuidance.prototype.initView = function () {
        console.log("ConDecDecisionGuidance initView");
        conDecObservable.subscribe(this);
    };

	ConDecDecisionGuidance.prototype.updateView = function () {
		conDecDecisionTable.updateView();
	};

	/*
	 * external usage: condec.decision.table
	 */
	ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function () {
		$("#recommendation-button").click(function(event) {
			event.preventDefault();
			$(this).prop("disabled",true);
			$("#recommendation-container tbody tr").remove() //TODO the rows are kept in the cache, but they should be removed completly
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");

			spinner.show();
			$("#recommendation-error").hide();
			conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), function(results, error) {

				if (error === null) {
					buildRecommendationTable(results);
				} else {
					$("#recommendation-error").show();
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();

			});
		});

	};

	function buildRecommendationTable(results) {
		const table = $("#recommendation-container tbody");

		let counter = 0;
		var sortedByScore = results.slice(0);
		sortedByScore.sort(function(a,b) {
			return b.score - a.score;
		});

		sortedByScore.forEach((recommendation) => {
				const alternative = recommendation.recommendations;
				let url = "";
				let tableRow = "";

				counter += 1;
				tableRow += "<tr>";
				tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.recommendations + "</a></td>";
				tableRow += "<td>" + recommendation.knowledgeSourceName + "</td>";
				tableRow += "<td>"+ recommendation.score +"%</td>";
				tableRow += "<td><button id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>Accept</button></td>";
				tableRow += "</tr>";
				table.append(tableRow);

				$(" #row_" + counter).click(function() {
					const currentIssue = conDecDecisionTable.getCurrentIssue();
					conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative",  recommendation.recommendations, "");
				});
		});
		conDecAPI.showFlag("success", "Results: " +  counter);
		//Since the data is added later, the table must be set to sortable afterwards
		AJS.tablessortable.setTableSortable(AJS.$("#recommendation-container"));
	}


	
	// export ConDecDecisionGuidance
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);