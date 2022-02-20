/**
 * Provides basic functions for managing recommendations (either of links 
 * or from external sources, also called decision guidance).
 */
(function(global) {

	let ConDecRecommendation = function() {
	};

	/**
	 * external usage: condec.link.recommendation.js, condec.decision.guidance.js
	 */
	ConDecRecommendation.prototype.buildScore = function(score, ID) {
		var scoreControl = document.getElementById("control-score-explanation");
		scoreControl.innerText = (score.value * 100).toFixed(0) + "%";

		var columns = "<tr style='background-color: #e8e8e8;'><td>" + score.explanation + "</td><td>" + (score.value * 100).toFixed(2) + "%</td></tr>";
		score.subScores.forEach(subScore => {
			columns += "<tr><td>" + subScore.explanation + "</td><td>" + subScore.value.toFixed(2) + "</td></tr>";
		});
		document.getElementById("score-explanation-table-body").innerHTML = columns;

		var scoreExplanation = scoreControl.outerHTML + document.getElementById("score-explanation").outerHTML;
		return scoreExplanation.replace(/score-explanation/g, "score-explanation-" + ID);
	};
	
	/**
	 * external usage: condec.link.recommendation.js
	 */
	ConDecRecommendation.prototype.getNumberOfNonDiscardedRecommendations = function(recommendations) {
		let numberOfNonDiscardedRecommendations = 0;
		for (let i in recommendations) {
			if (!recommendations[i].isDiscarded) {
				numberOfNonDiscardedRecommendations++;
			}
		}
		return numberOfNonDiscardedRecommendations;
	};

	global.conDecRecommendation = new ConDecRecommendation();
})(window);