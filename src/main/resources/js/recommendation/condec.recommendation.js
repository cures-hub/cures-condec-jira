/**
 * This module provides basic functions for managing recommendations (either of links 
 * or from external sources, also called decision guidance).
 */
(function(global) {

	let ConDecRecommendation = function() {
	};

	ConDecRecommendation.prototype.buildScore = function(score, ID) {
		var scoreControl = document.getElementById("control-score-explanation");
		scoreControl.innerText = score.value.toFixed(0) + "%";

		var columns = "<tr style='background-color: #e8e8e8;'><td>" + score.explanation + "</td><td>" + score.value.toFixed(2) + "%</td></tr>";
		score.subScores.forEach(subScore => {
			columns += "<tr><td>" + subScore.explanation + "</td><td>" + subScore.value.toFixed(2) + "</td></tr>";
		})
		document.getElementById("score-explanation-table-body").innerHTML = columns;

		var scoreExplanation = scoreControl.outerHTML + document.getElementById("score-explanation").outerHTML;
		return scoreExplanation.replace(/score-explanation/g, "score-explanation-" + ID);
	}

	global.conDecRecommendation = new ConDecRecommendation();
})(window);