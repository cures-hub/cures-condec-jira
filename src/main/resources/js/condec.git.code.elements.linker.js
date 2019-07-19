function linkBranchCandidates(
  rationale,
  branchName,
  branchInSequence,
  problemSourceCategory
) {
  // bad problem explanations
  var ARGUMENT_WITHOUT_PARENT_ELEMENT = "Argument without parent alternative";
  var ALTERNATIVE_DECISION_WITHOUT_PARENT_ELEMENT =
    "Alternative without parent problem";
  var ISSUE_WITH_MANY_DECISIONS = "Issue has too many decisions";
  var DECISION_WITHOUT_PRO_ARGUMENTS = "Decision does not have a pro argument";

  // not that bad problem explanations
  var ALTERNATIVE_DECISION_WITHOUT_ARGUMENTS =
    "Alternative does not have any arguments";
  var DECISION_ARGUMENTS_MAYBE_WORSE_THAN_ALTERNATIVE =
    "Decision's arguments seem weaker than in one of sibling alternatives";
  var ISSUE_WITHOUT_DECISIONS = "Issue does not have a decision yet";
  var ISSUE_WITHOUT_ALTERNATIVES = "Issue does not have any alternatives yet";

  var rationale = rationale;
  var branchName = branchName;
  var branchInSequence = branchInSequence;
  var problemSourceCategory = problemSourceCategory;

  // paser helpers
  var currentIssueId = null;
  var currentAlternativeId = null;
  var currentArgumentId = null; // will not be used at all?
  var currentSource = null;
  var nextRationaleExpectedOnLine = null;

  // results
  var problemsList = [];
  var linkCandidates = [];

  function resetHelpers() {
    currentIssueId = null;
    currentAlternativeId = null;
    currentArgumentId = null;
    currentSource = null;
    nextRationaleExpectedOnLine = null;
  }

  function runCodelinker() {
    console.log("runCodelinker");
    // map sorted rationale elements to simpler structure
    linkCandidates = rationale.map(function(el, idx) {
      candidate = {};
      candidate.id = idx;
      candidate.rationaleHash = el.key.rationaleHash;
      candidate.rationaleType = el.type;
      candidate.source = el.key.source;
      candidate.positionCursor = el.key.positionCursor;
      if (!el.key.sourceTypeCommitMessage) {
        candidate.positionEndLine = el.key.positionEndLine;
      }
      candidate.positionStartLine = el.key.positionStartLine;
      // we are interested only in final code elements
      candidate.skip = el.key.codeFileA;
      return candidate;
    });

    if (linkCandidates !== null && linkCandidates.length) {
      inferElementLinks();
      captureProblems();
      attachProblemsToElementsInHTML();
    }

    function setStatusWarningInMenuItem() {
      setStatusInMenuItem("condec-attention");
    }

    function setStatusFineInMenuItem() {
      setStatusInMenuItem("condec-fine");
    }

    function setStatusInMenuItem() {
      var menuItems = document.getElementsByClassName("menu-item");
      if (menuItems && menuItems.length && menuItems.length > 0) {
        for (var idx = 0; idx < menuItems.length; idx++) {
          if (menuItems[idx].dataset.condec) {
            menuItems[idx].classList.add("condec-attention");
          }
        }
      }
    }
    function inferElementLinks() {
      for (var i = 0; i < linkCandidates.length; i++) {
        element = linkCandidates[i];

        if (element.skip) continue;
        switch (element.rationaleType.toLowerCase()) {
          case "issue":
            addNewIssue(element);
            break;

          case "decision":
            addNewAlternative(element, true);
            break;

          case "alternative":
            addNewAlternative(element, false);
            break;

          case "pro":
            addNewArgument(element, true);
            break;

          case "con":
            addNewArgument(element, false);
            break;

          default:
            console.warn("Unknown rationale type.");
            break;
        }
      }
    }

    function captureProblems() {
      // reset problems list
      problemsList = [];
      for (var i = 0; i < linkCandidates.length; i++) {
        if (linkCandidates[i].skip) {
          continue;
        }
        var problemsInElement = linkCandidates[i].getElementProblems();
        if (problemsInElement.hasAnyProblems()) {
          problemsList.push({ elementId: i, problems: problemsInElement });
        }
      }
    }

    function attachProblemsToElementsInHTML() {
      if (problemsList.length === 0) {
        return;
      }
      var errorsGroup = new Set();
      var warningsGroup = new Set();
      for (var i = 0; i < problemsList.length; i++) {
        var problemWithRationale = problemsList[i];
        var targetId = composeId(problemWithRationale);
        var target = document.getElementById(targetId);
        if (!target) {
          console.error("Could not find element with ID" + targetId);
          return;
        }
        var problemElement = document.createElement("p");
        problemElement.className = "rat-link-problem";
        problemElement.title = "";

        // has warnings?
        if (problemWithRationale.problems.warnings.length > 0) {
          problemElement.className = problemElement.className + " warning";
          problemElement.title += problemWithRationale.problems.warnings.toString();
          problemElement.innerText += problemElement.title;
          for (
            var w = 0;
            w < problemWithRationale.problems.warnings.length;
            w++
          ) {
            warningsGroup.add(problemWithRationale.problems.warnings[w]);
          }
        }
        // has errors?
        if (problemWithRationale.problems.errors.length > 0) {
          problemElement.className = problemElement.className + " error";
          problemElement.title += problemWithRationale.problems.errors.toString();
          problemElement.innerText += problemElement.title;

          for (
            var e = 0;
            e < problemWithRationale.problems.errors.length;
            e++
          ) {
            errorsGroup.add(problemWithRationale.problems.errors[e]);
          }
        }
        target.appendChild(problemElement);
      }
      appendElementsToQualitySummary(errorsGroup, warningsGroup);
    }

    function appendElementsToQualitySummary(errors, warnings) {
      var id = "branchGroup-" + branchInSequence + "-qualitySummary";
      var qElem = document.getElementById(id);
      if (!qElem) {
        console.error("Could not find element with id: " + id);
        return;
      }
      if (errors.size > 0 || warnings.size > 0) {
        // reset the initial text
        qElem.childNodes[0].textContent = "";
        // draw attention to feature tab menu item
        setStatusWarningInMenuItem();
      } else if (errors.size == 0 && warnings.size == 0) {
        setStatusFineInMenuItem();
      }

      if (errors.size > 0) {
        qElem.classList.add("hasErrors");
        appendProblemCategoryToQualitySummary(qElem, errors, "Errors in ");
      }
      if (warnings.size > 0) {
        qElem.classList.add("hasWarnings");
        appendProblemCategoryToQualitySummary(qElem, warnings, "Warnings in ");
      }
    }

    function appendProblemCategoryToQualitySummary(qElem, categorySet, prefix) {
      var catElem = document.createElement("p");
      var problemsAsList = Array.from(categorySet).join("; ");
      catElem.innerText =
        prefix + problemSourceCategory + ": " + problemsAsList;
      qElem.appendChild(catElem);
    }

    /*
		TODO: move to git diff utils
		*/
    function composeId(problemStructure) {
      var arrayEllement = linkCandidates[problemStructure.elementId];
      if (!arrayEllement) {
        return "";
      }
      var htmlID =
        arrayEllement.rationaleHash +
        "-" +
        branchName +
        "-" +
        arrayEllement.source;
      return btoa(htmlID);
    }

    function addNewIssue(el) {
      // manage parser variables
      resetHelpers();
      currentIssueId = el.id;
      currentSource = el.source;
      increaseNextRationaleLatestExpectedLine(el);

      // properties
      el.alternatives = [];
      el.decisions = [];
      el.problems = initProblems();

      /* methods */
      el.addAlternative = function(subElement, isDecision) {
        if (isDecision) {
          this.decisions.push(subElement.id);
        } else {
          this.alternatives.push(subElement.id);
        }
      };
      // has too many decisions
      el.hasManyDecisions = function() {
        return this.decisions.length > 1;
      };
      // has no decisions
      el.hasNoDecisions = function() {
        return this.decisions.length === 0;
      };
      // has no alternatives
      el.hasNoAlternatives = function() {
        return this.alternatives.length === 0;
      };

      // gather element's problems
      el.getElementProblems = function() {
        if (this.hasManyDecisions()) {
          this.problems.errors.push(ISSUE_WITH_MANY_DECISIONS);
        }
        if (this.hasNoAlternatives()) {
          this.problems.warnings.push(ISSUE_WITHOUT_ALTERNATIVES);
        }
        if (this.hasNoDecisions()) {
          this.problems.warnings.push(ISSUE_WITHOUT_DECISIONS);
        }
        return this.problems;
      };
    }

    function addNewAlternative(el, isDecision) {
      el.parent = null;

      // manage parser variables
      if (canBeLinkedWithParent(el)) {
        if (
          linkCandidates[currentIssueId] &&
          linkCandidates[currentIssueId].addAlternative
        ) {
          el.parent = currentIssueId;
          linkCandidates[currentIssueId].addAlternative(el, isDecision);
        }
      } else {
        resetHelpers();
      }
      currentAlternativeId = el.id;
      currentSource = el.source;
      increaseNextRationaleLatestExpectedLine(el);

      // properties
      el.pros = [];
      el.cons = [];
      el.problems = initProblems();
      el.isDecision = isDecision;

      /* methods */
      el.addNewArgument = function(subElement, isPro) {
        if (isPro) {
          this.pros.push(subElement.id);
        } else {
          this.cons.push(subElement.id);
        }
      };
      //has parent
      el.hasParent = function() {
        return this.parent !== null;
      };
      // sums up and down votes
      el.countVotes = function() {
        return this.pros.length - this.cons.length;
      };
      // checks if decision has any up votes
      el.isDecisionWithoutPros = function() {
        return this.isDecision && this.pros.length === 0;
      };
      // checks if decision is out voted by any alternative
      el.isDecisionOutvotedByAlternative = function() {
        if (!this.isDecision || !this.hasParent()) return false;

        var parentAlternatives = linkCandidates[this.parent].alternatives;
        if (parentAlternatives.length > 0)
          for (var i = 0; i < parentAlternatives.length; i++) {
            var alternativeId = parentAlternatives[i];
            if (this.countVotes() < linkCandidates[alternativeId].countVotes())
              return true;
          }
        return false;
      };
      // checks if alternative has any votes
      el.isWithoutArguments = function() {
        return this.pros.length === 0 && this.cons.length === 0;
      };

      // gather element's problems
      el.getElementProblems = function() {
        if (!this.hasParent()) {
          this.problems.errors.push(
            ALTERNATIVE_DECISION_WITHOUT_PARENT_ELEMENT
          );
        }
        if (this.isDecisionWithoutPros()) {
          this.problems.errors.push(DECISION_WITHOUT_PRO_ARGUMENTS);
        }
        if (this.isDecisionOutvotedByAlternative()) {
          this.problems.warnings.push(
            DECISION_ARGUMENTS_MAYBE_WORSE_THAN_ALTERNATIVE
          );
        }
        if (this.isWithoutArguments()) {
          this.problems.warnings.push(ALTERNATIVE_DECISION_WITHOUT_ARGUMENTS);
        }
        return this.problems;
      };
    }

    function addNewArgument(el, isProArg) {
      el.parent = null;

      if (canBeLinkedWithParent(el)) {
        if (
          linkCandidates[currentAlternativeId] &&
          linkCandidates[currentAlternativeId].addNewArgument
        ) {
          el.parent = currentAlternativeId;
          linkCandidates[currentAlternativeId].addNewArgument(el, isProArg);
        }
      } else {
        resetHelpers();
      }
      currentArgumentId = el.id;
      currentSource = el.source;
      increaseNextRationaleLatestExpectedLine(el);

      // properties
      el.isProArg = isProArg;
      el.problems = initProblems();

      /* methods */
      //has parent
      el.hasParent = function() {
        return this.parent !== null;
      };

      // gather element's problems
      el.getElementProblems = function() {
        if (!this.hasParent()) {
          this.problems.errors.push(ARGUMENT_WITHOUT_PARENT_ELEMENT);
        }
        return this.problems;
      };
    }

    function canBeLinkedWithParent(element) {
      // check element's source
      if (currentSource === null || currentSource !== element.source) {
        return false;
      }
      // check line distances for rationale in code, not in commit messages
      if (
        element.positionEndLine &&
        (nextRationaleExpectedOnLine === null ||
          nextRationaleExpectedOnLine < element.positionStartLine)
      ) {
        return false;
      }
      return true;
    }

    // can be only applied if line end is known, which is not
    // the case for rationale from commit messages
    function increaseNextRationaleLatestExpectedLine(element) {
      if (!element.positionEndLine) {
        return;
      }
      var MAX_DISTANCE = 2; // allows one line between rationale
      nextRationaleExpectedOnLine = MAX_DISTANCE + element.positionEndLine;
    }

    function initProblems() {
      var problemStructure = {
        errors: [],
        warnings: [],
        hasAnyProblems: function() {
          return this.errors.length > 0 || this.warnings.length > 0;
        }
      };
      return problemStructure;
    }
  }
  runCodelinker();
}
