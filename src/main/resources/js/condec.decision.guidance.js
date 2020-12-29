(function (global) {

	var recommendations;
	var globalCounter;
	var idOfExistingElement;
	var documentationLocationOfExistingElement;

	let ConDecDecisionGuidance = function ConDecDecisionGuidance() {
		this.globalCounter = 0;
		this.recommendations = []
		this.idOfExistingElement = 0;
		this.documentationLocationOfExistingElement = "s"

	};

	ConDecDecisionGuidance.prototype.initView = function () {
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
			const currentIssue = conDecDecisionTable.getCurrentIssue();
			$(this).prop("disabled",true);
			$("#recommendation-container tbody tr").remove() //TODO the rows are kept in the cache, but they should be removed completly
			const keyword = $("#recommendation-keyword");
			const spinner = $("#loading-spinner-recommendation");

			spinner.show();
			$("#recommendation-error").hide();
			conDecAPI.getRecommendation(conDecAPI.getProjectKey(), keyword.val(), currentIssue.id, currentIssue.documentationLocation ,  function(results, error) {

				if (error === null) {
					buildRecommendationTable(results);
				}
				$("#recommendation-button").prop("disabled",false);
				spinner.hide();

			});
		});

	};

	function buildRecommendationTable(results) {
		conDecDecisionGuidance.recommendations = results;
		const table = $("#recommendation-container tbody");

		let counter = 0;
		var sortedByScore = results.slice(0);
		sortedByScore.sort(function(a,b) {
			return b.score.score - a.score.score;
		});

		sortedByScore.forEach((recommendation) => {
				const localCounter = counter;
				counter += 1;
				const alternative = recommendation.recommendation;
				let url = "";
				let tableRow = "";


				tableRow += "<tr>";
				tableRow += "<td><a class='alternative-summary' href='" + recommendation.url + "'>" + recommendation.recommendation + "</a></td>";
				tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" + recommendation.knowledgeSourceName + "<span class='aui-icon aui-icon-small "  + recommendation.icon + "'>Knowledge Source Type</span></div></td>";
				tableRow += "<td>"+ buildScore(recommendation.score, recommendation.score.score) +"</td>";
				tableRow += "<td><button title='Adds the recommendation to the knowledge graph' id='row_" + counter + "' class='aui-button-primary aui-button accept-solution-button'>" +  "Accept" + "</button></td>";
				tableRow += "<td>";
				recommendation.arguments.forEach((argument) => {
					if(argument) {
						tableRow += "<img src='" + argument.image + "'/>";
						tableRow += argument.summary;
					}
				});
				tableRow += "</td>";
				tableRow += "</tr>";
				table.append(tableRow);

				$(" #row_" + counter).click(function() {
					conDecDecisionGuidance.globalCounter = localCounter;
					const currentIssue = conDecDecisionTable.getCurrentIssue();
					conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation, "Alternative",  recommendation.recommendation, "", function(id, documentationLocation) {
							conDecDecisionGuidance.idOfExistingElement = id;
							conDecDecisionGuidance.documentationLocationOfExistingElement = documentationLocation;

							recommendation.arguments.forEach(argument => {
									conDecAPI.createDecisionKnowledgeElement(argument.summary, "", argument.type, argument.documentationLocation, id, documentationLocation, function() {
										conDecAPI.showFlag("success", "Recommendation was added successfully!");
									});
							});
					});
					/*recommendation.arguments.forEach((argument) => {

						if(argument.type == "Con") {
						  conDecAPI.createDecisionKnowledgeElement(argument.summary, "", "Con", currentIssue.documentationLocation,
										currentIssue.id, currentIssue.documentationLocation, function (id) {
											conDecObservable.notify();
										});
						}
						if(argument.type == "Pro") {
						  conDecAPI.createDecisionKnowledgeElement(argument.summary, "", "Pro", currentIssue.documentationLocation,
                                         currentIssue.id, currentIssue.documentationLocation, function (id) {
													conDecObservable.notify();
												});
						}
					})*/
				});
		});
		conDecAPI.showFlag("success", "Results: " +  counter);
		//Since the data is added later, the table must be set to sortable afterwards
		AJS.tablessortable.setTableSortable(AJS.$("#recommendation-container"));
	}

	function buildScore(scoreObject, ID) {
		const scoreControl =  "<a data-aui-trigger aria-controls='"+ ID +"' href='"+ ID +"'>" +
								 + scoreObject.score.toFixed(2) + "%"+
								 "</a>";

		var inlineDialog = "<aui-inline-dialog id='" + ID + "' responds-to='hover'>";
		inlineDialog += "<div class='description'>The Score is composed by different aspects. The table gives an overview of the used components, that are used to calculate the score</div>";
		inlineDialog += "<table>";
		inlineDialog += "<thead>";
		inlineDialog += "<th>Description</th>";
		inlineDialog += "<th>Score</th>";
		inlineDialog += "</thead>";
		inlineDialog += "<tbody>";
		scoreObject.composedScore.forEach(score => {
			inlineDialog += "<tr>";
			inlineDialog += "<td>" + score.explanation + "</td><td>" + score.score.toFixed(2)  +  "</td>";
			inlineDialog += "</tr>";
		})
		inlineDialog += "</tbody>";
		inlineDialog += "</table>";
		inlineDialog += "<span class='project-config-webpanel-column-content'></span>";
		inlineDialog += "<p><b>Score: "  + scoreObject.score.toFixed(2)  +  "%</b></p>";

		inlineDialog += "</aui-inline-dialog>";

		return scoreControl + inlineDialog;
	 }


	
	// export ConDecDecisionGuidance
	global.conDecDecisionGuidance = new ConDecDecisionGuidance();

})(window);