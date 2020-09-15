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
			const table = $("#recommendation-container tbody");
			$("#recommendation-container tbody tr").remove()
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");

			spinner.show();

			conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), function(results, error) {
				let counter = 0;
				if (error === null) {

					var sortedByScore = results.slice(0);
                    sortedByScore.sort(function(a,b) {
                        return b.score - a.score;
                    });

					sortedByScore.forEach((recommendation) => {
							const alternative = recommendation.recommendations;
							let url = "";
							let tableRow = "";

							if (alternative.url) { url = alternative.url }
							counter += 1;
							tableRow += "<tr>";
							tableRow += "<td><a class='alternative-summary' href='" + url + "'>" + alternative.summary + "</a></td>";
							tableRow += "<td>" + recommendation.knowledgeSourceName + "</td>";
							tableRow += "<td>"+ recommendation.score +"</td>";
							tableRow += "<td><button id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>Accept</button></td>";
							tableRow += "</tr>";
							table.append(tableRow);

							$(" #row_" + counter).click(function() {
								const currentIssue = conDecDecisionTable.getCurrentIssue();
                            	conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative",  alternative.summary, alternative.description);
                            });
					});
					conDecAPI.showFlag("success", "Results: " +  counter);
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();
			});
		});

	};


	
	// export ConDecDecisionGuidance
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);