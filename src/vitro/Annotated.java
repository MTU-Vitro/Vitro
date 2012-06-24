package vitro;

import java.util.*;

/**
* Annotated Agents can provide supplementary
* information to their view.
* While any class can implement this interface,
* controllers specifically track and expose
* the annotations provided by Annotated Agents.
*
* @author John Earnest
**/
public interface Annotated {
	
	/**
	* @return a set of Annotations for the current timestep.
	**/
	public Set<Annotation> annotations();

}