package vitro.tools;

import java.awt.*;

public abstract class Slide {
	public enum Transition { None, SlideUp, SlideDown, SlideRight, SlideLeft };
	private Transition transition;

	public Transition getTransition()       { return transition; }
	public void setTransition(Transition t) { transition = t; }

	void paint(Graphics2D g, Dimension size) {}
	void tick()            {}
	void gotFocus()        {}
	void lostFocus()       {}
}