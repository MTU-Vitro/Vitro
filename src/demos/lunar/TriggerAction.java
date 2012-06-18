package demos.lunar;

import vitro.*;

/**
*
**/
public class TriggerAction implements Action {
	private final Lander lander;

	private boolean applied = false;
	private boolean mTriggered = false;
	private boolean lTriggered = false;
	private boolean rTriggered = false;

	/**
	*
	**/
	public TriggerAction(Lander lander) {
		this.lander = lander;
	}

	/**
	*
	**/
	public void setMainTrigger(boolean on) {
		if(!applied) { mTriggered = on; }
	}

	/**
	*
	**/
	public void setLeftTrigger(boolean on) {
		if(!applied) { lTriggered = on; }
	}

	/**
	*
	**/
	public void setRightTrigger(boolean on) {
		if(!applied) { rTriggered = on; }
	}

	/**
	*
	**/
	public void apply() {
		if(!applied) { applied = true; }

		lander.mThruster.triggered = mTriggered;
		lander.lThruster.triggered = lTriggered;
		lander.rThruster.triggered = rTriggered;
	}

	/**
	*
	**/
	public void undo() {
		lander.lThruster.triggered = false;
		lander.rThruster.triggered = false;
		lander.mThruster.triggered = false;
	}

	/**
	* Factor in parametric values?
	**/
	@Override
	public int hashCode() {
		return lander.hashCode();
	}

	/**
	* Factor in parametric values?
	**/
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TriggerAction)) { return false; }
		TriggerAction other = (TriggerAction)o;
		return other.lander.equals(lander);
	}

	/**
	* Factor in parametric values?
	**/
	//@Override
	//public String toString() { return ""; }
}
