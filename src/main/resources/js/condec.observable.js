/**
 * Provides the notification method for the ConDec views. This module is the subject/observable 
 * according to the observer design pattern. The views/observers subscribe/register to this observable. 
 * The views need to implement an updateView function. 
 * The updateView functions of the subscribed views are called in the notify function.
 *
 * Registered/subscribed views/observers e.g. are conDecKnowledgePage, conDecChronology, 
 * and many other views that implement an updateView() function.
 *   
 * Is required by all views that are registered/subscribed as observers.
 */
(function(global) {

	var observers = null;

	var ConDecObservable = function() {
		this.observers = [];
	};

	ConDecObservable.prototype.notify = function() {
		this.observers.forEach(function(observer) {
			if (typeof observer.updateView === "function") {
				observer.updateView();
			} else {
				console.log(observer + " is not a valid view. You need to implement the updateView method.");
			}
		});
	};

	ConDecObservable.prototype.subscribe = function(observer) {
		this.observers.push(observer);
	};

	global.conDecObservable = new ConDecObservable();
})(window);