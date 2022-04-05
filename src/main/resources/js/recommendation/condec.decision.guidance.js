/**
 * Implements the view for solution option recommendation for decision problems (=decision
 * guidance).
 * The recommended solution options are taken from external knowledge sources, such as
 * other Jira projects or DBPedia.
 *
 * Is referenced in HTML by
 * tabs/recommendation/decisionGuidance.vm
 */
/* global conDecAPI, conDecDialog, conDecDecisionGuidanceAPI, conDecDecisionGuidance,
   conDecRecommendation, conDecNudgingAPI, conDecObservable, conDecFiltering */
(function(global) {
    const ConDecDecisionGuidance = function() { };

    ConDecDecisionGuidance.prototype.initView = function() {
        // get all the decision problems for the dropdown and fill the dropdown
        const dropdown = document.getElementById("decision-guidance-dropdown");
        conDecAPI.getDecisionProblems({}, (decisionProblems) =>
            conDecFiltering.initKnowledgeElementDropdown(dropdown, decisionProblems,
                this.selectedDecisionProblem,
                "decision-guidance", (selectedElement) => {
                    conDecDecisionGuidance.selectedDecisionProblem = selectedElement;
                }));

        // add button listeners
        this.addOnClickListenerForRecommendations();
        this.addOnClickListenerForManageDiscarded();

        // Register/subscribe this view as an observer
        conDecObservable.subscribe(this);
    };

    ConDecDecisionGuidance.prototype.updateView = function() {
    };

    function onAcceptClicked(recommendation, parentElement) {
        conDecAPI.getKnowledgeElement(parentElement.id, parentElement.documentationLocation,
            (currentIssue) => {
                conDecDialog.showCreateDialog(currentIssue.id, currentIssue.documentationLocation,
                    "Alternative", recommendation.summary, "",
                    (id, documentationLocation) => {
                        recommendation.arguments.forEach((argument) => {
                            conDecAPI.createDecisionKnowledgeElement(argument.summary, "",
                                argument.type, argument.documentationLocation, id,
                                documentationLocation,
                                () => {
                                    conDecAPI.showFlag("success",
                                        "Recommendation was added successfully!");
                                });
                        });
                    });
            });
    }

    function buildRecommendationTable(recommendations, parentElement) {
        const tableBody = document.getElementById("recommendation-container-table-body");
        const checkBoxShowDiscarded = document.getElementById("checkbox-show-discarded");
        checkBoxShowDiscarded.onclick = function() {
            buildRecommendationTable(recommendations, parentElement);
        };
        tableBody.innerHTML = "";
        let counter = 0;
        let counterHidden = 0;
        recommendations.forEach((recommendation) => {
            if (!checkBoxShowDiscarded.checked && recommendation.isDiscarded) {
                counterHidden++;
                return;
            }
            counter++;
            let tableRow;
            if (recommendation.isDiscarded) {
                recommendation.arguments = [];  // Ignore arguments of discarded recommendations
                tableRow = "<tr class = \"discarded\">";
            } else {
                tableRow = "<tr>";
            }
            tableRow += `<td><a class='alternative-summary' href='${recommendation.url}'>` +
                `${recommendation.summary}</a></td>`;
            tableRow += "<td><div style='display:flex;gap:3px;align-items:center;'>" +
                `${recommendation.knowledgeSource.name}<span class='aui-icon aui-icon-small ` +
                `${recommendation.knowledgeSource.icon}'>Knowledge Source Type</span></div></td>`;
            tableRow += "<td>" +  // eslint-disable-line prefer-template
                //                    (we would have `` inside ``)
                conDecRecommendation.buildScore(recommendation.score, `score_${counter}`) +
                "</td>";
            if (recommendation.isDiscarded) {
                tableRow += `<td><button title='${conDecDecisionGuidance.UNDO_DESCRIPTION}' ` +
                    `id='undo_discard_${counter}' ` +
                    "class='aui-button-primary aui-button accept-solution-button'>" +
                    "<span class='aui-icon aui-icon-small aui-iconfont-undo'></span>" +
                    `${conDecDecisionGuidance.UNDO_TITLE}</button></td>`;
            } else {
                tableRow += `<td><button title='${conDecDecisionGuidance.ACCEPT_DESCRIPTION}' ` +
                    `id='row_${counter}' ` +
                    "class='aui-button-primary aui-button accept-solution-button'>" +
                    `${conDecDecisionGuidance.ACCEPT_TITLE}</button>`;
                tableRow += `<button title='${conDecDecisionGuidance.DISCARD_DESCRIPTION}' ` +
                    `id='discard_${counter}' ` +
                    "class='aui-button-primary aui-button accept-solution-button'>" +
                    "<span class='aui-icon aui-icon-small aui-iconfont-trash'></span>" +
                    `${conDecDecisionGuidance.DISCARD_TITLE}</button></td>`;
            }
            tableRow += "<td><ul>";
            recommendation.arguments.forEach((argument) => {
                if (argument) {
                    tableRow += `<li><img src='${argument.image}'/>${argument.summary}</li>`;
                }
            });
            tableRow += "</ul></td>";
            tableRow += "</tr>";
            tableBody.insertAdjacentHTML("beforeend", tableRow);

            if (recommendation.isDiscarded) {
                $(`#undo_discard_${counter}`).click(() => {
                    conDecDecisionGuidanceAPI.undoDiscardRecommendation(recommendation);
                    buildRecommendationTable(recommendations, parentElement);
                });
            } else {
                $(`#row_${counter}`).click(() => {
                    onAcceptClicked(recommendation, parentElement);
                });
                $(`#discard_${counter}`).click(() => {
                    conDecDecisionGuidanceAPI.discardRecommendation(recommendation);
                    buildRecommendationTable(recommendations, parentElement);
                });
            }
        });
        const spanCountHidden = document.getElementById("count-hidden-recommendations");
        if (counterHidden > 0) {
            spanCountHidden.innerHTML = `(${counterHidden})`;
        } else {
            spanCountHidden.innerHTML = "";
        }
        conDecAPI.showFlag("success", `#Recommendations: ${counter}`);
    }

    ConDecDecisionGuidance.prototype.addOnClickListenerForRecommendations = function() {
        const tableBody = document.getElementById("recommendation-container-table-body");
        $("#recommendation-button").click((event) => {
            event.preventDefault();
            tableBody.innerHTML = "";
            const spinner = $("#loading-spinner-recommendation");
            const keywords = document.getElementById("recommendation-keywords").value;
            spinner.show();
            conDecDecisionGuidance.selectedDecisionProblem.projectKey = conDecAPI.projectKey;
            Promise.resolve(conDecDecisionGuidanceAPI.getRecommendations(
                conDecDecisionGuidance.selectedDecisionProblem, keywords))
                .then((recommendations) => {
                    if (recommendations.length > 0) {
                        buildRecommendationTable(recommendations,
                            conDecDecisionGuidance.selectedDecisionProblem);
                    } else {
                        tableBody.innerHTML = "<i>No recommendations found!</i>";
                    }
                    conDecNudgingAPI.decideAmbientFeedbackForTab(recommendations.length,
                        "menu-item-decision-guidance");
                    spinner.hide();
                })
                .catch((err) => {
                    spinner.hide();
                    tableBody.innerHTML = "<strong>An error occurred!</strong>";
                });
        });
    };

    /**
     * Set on-click behaviour for button "manage discarded recommendations":
     * Send request to get all discarded recommendations for the selected decision problem and
     * show the obtained discarded recommendations in the recommendation table.
     */
    ConDecDecisionGuidance.prototype.addOnClickListenerForManageDiscarded = function() {
        const tableBody = document.getElementById("recommendation-container-table-body");
        const checkboxShowDiscarded = document.getElementById("checkbox-show-discarded");
        $("#manage-discarded-button").click((event) => {
            event.preventDefault();
            tableBody.innerHTML = "";
            const spinner = $("#loading-spinner-recommendation");
            spinner.show();
            conDecDecisionGuidance.selectedDecisionProblem.projectKey = conDecAPI.projectKey;
            Promise.resolve(conDecDecisionGuidanceAPI.getDiscardedRecommendations(
                conDecDecisionGuidance.selectedDecisionProblem))
                .then((recommendations) => {
                    if (recommendations.length > 0) {
                        buildRecommendationTable(recommendations,
                            conDecDecisionGuidance.selectedDecisionProblem);
                    } else {
                        tableBody.innerHTML = "<i>No recommendations found!</i>";
                    }
                    spinner.hide();
                })
                .catch((err) => {
                    spinner.hide();
                    tableBody.innerHTML = "<strong>An error occurred!</strong>";
                });
            if (!checkboxShowDiscarded.checked) {
                checkboxShowDiscarded.click();
            }
        });
    };

    global.conDecDecisionGuidance = new ConDecDecisionGuidance();
})(window);
