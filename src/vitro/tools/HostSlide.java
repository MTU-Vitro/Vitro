package vitro.tools;

import java.awt.*;
import vitro.*;

public class HostSlide extends Slide {
	public final View view;

	public HostSlide(View view, Slide.Transition transition) {
		setTransition(transition);
		this.view = view;
	}

	public HostSlide(View view) {
		this(view, Slide.Transition.None);
	}

	void paint(Graphics2D g, Dimension size) {
		g.translate(
			(size.getWidth()  - view.width())  / 2,
			(size.getHeight() - view.height()) / 2
		);
		view.draw(g);
	}

	void tick() {
		view.tick(.1);
	}

	void gotFocus() {
		
	}

	void lostFocus() {
		
	}
}