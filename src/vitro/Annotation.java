package vitro;

/**
* Annotations provide a generic way for Agents and other
* elements of a Model to communicate supplementary information
* to Views and other components.
*
* In many situations, an Agent has some state that could
* be usefully visualized- for example, the path that
* the Agent's Actors intend to take in the next few timesteps.
*
* Every View may have a unique way of drawing Annotations,
* or it may choose not to draw certain kinds of annotations
* at all. This allows View implementations to choose the "best"
* way of presenting Annotations, and avoids complicating
* Agents with any of the mechanical details of drawing
* graphics.
*
* @author John Earnest
**/

public interface Annotation {
	
}