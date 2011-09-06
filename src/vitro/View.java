package vitro;

import java.awt.Graphics2D;

/**
* A View provides a visualization of the current
* state of a Model and may further visualize
* Actions as they modify this state.
*
* Views may also show information regarding the
* state of one or more Agents if the Agents implement
* the Annotated interface.
*
* Default views are provided for each major
* Model type, and users needing a more specific
* implementation may extend these or build their own
* from scratch.
*
* @author John Earnest
**/
public interface View {

	/**
	* Obtain a reference to the Controller
	* associated with this View.
	*
	* @return this view's Controller.
	**/
	public Controller controller();

	/**
	* Obtain the ColorScheme used by this View.
	* Mainly used so that host applications can customize
	* their UI overlays to suit this View.
	*
	* @return this view's ColorScheme.
	**/
	public ColorScheme colorScheme();

	/**
	* Obtain the preferred width of this View, in pixels.
	*
	* @return the width of this view in pixels.
	**/
	public int width();

	/**
	* Obtain the preferred height of this View, in pixels.
	*
	* @return the height of this view in pixels.
	**/
	public int height();

	/**
	* Draw this view.
	* This method may be called multiple times
	* per call to "tick()".
	*
	* @param g the target Graphics2D surface.
	**/
	public void draw(Graphics2D g);

	/**
	* Advance the state of the simulation.
	* The time provided may be in fractional
	* units. When a fixed amount of time
	* has elapsed, the View should direct
	* the associated controller to advance
	* the simulation state.
	*
	* @param time the amount of time that has passed since the last tick
	**/
	public void tick(double time);

	/**
	* Release any transient view state and
	* update the View to reflect the current
	* state of the Model.
	* This will be called whenever the Model's
	* Controller is driven externally, allowing
	* Views to interrupt any animations or
	* other operations in progress.
	**/
	public void flush();
}