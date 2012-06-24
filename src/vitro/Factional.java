package vitro;

/**
* The Factional interface is used to indicate that
* an element can belong to a 'team' in some way.
* When applied to a Model, Controllers use this
* method to determine which team controls the
* current turn. Different methods of changing what
* value team() returns can produce different
* turn-taking behaviors, such as round-robin,
* lowest-score-first or random turns.
*
* When applied to Actors, the team() method is
* used to determine what team the Actor belongs
* to, and thus decide whether the Actor should
* be allowed to move. See the documentation
* for Controller for more information.
*
* @author John Earnest
**/
public interface Factional {

	/**
	* Which team is this object associated with?
	* The integer returned is just a team identifier
	* and need only be consistent between members
	* of the same team.
	*
	* @return the current team id
	**/
	public int team();

}