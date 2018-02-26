package de.uhd.ifi.se.decision.documentation.jira.util;

/**
 * @description Implementation of a pair data structure
 */
public class Pair<Left, Right> {

	private final Left left;

	private final Right right;

	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}

	public Left getLeft() {
		return left;
	}

	public Right getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Pair))
			return false;
		Pair<?, ?> pairObject = (Pair<?, ?>) object;
		return this.left.equals(pairObject.getLeft()) && this.right.equals(pairObject.getRight());
	}
}