package edu.udayton.udri.stash.hook;

public enum Rule {
	/**
	 * Change is allowed to branch if done through pull request.
	 */
	PULL_REQUEST,
	
	/**
	 * Change is allowed to branch if merge commit with no conflicts.
	 */
	NO_CONFLICT_MERGE
}
