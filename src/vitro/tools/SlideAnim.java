package vitro.tools;
import java.awt.*;

public class SlideAnim implements Anim {

	private enum State { First, Tween, Second };
	private final Anim a;
	private final Anim b;

	private State  state = State.First;
	private double sofar = 0.0;

	public SlideAnim(Anim a, Anim b) {
		this.a   = a;
		this.b   = b;
	}

	public void tick(double time) {
		switch(state) {
			case First:  a.tick(time);  if (a.done())     { state = State.Tween; }  return;
			case Second: b.tick(time);                                              return;
			case Tween:  sofar += time; if (sofar >= 1.0) { state = State.Second; } return;
		}
	}

	public void draw(Graphics2D g, int width, int height) {
		switch(state) {
			case First:  a.draw(g, width, height); return;
			case Second: b.draw(g, width, height); return;
			case Tween:
				Graphics2D g1 = (Graphics2D)g.create();
				Graphics2D g2 = (Graphics2D)g.create();
				g1.translate(-width *      sofar , 0);
				g2.translate( width * (1 - sofar), 0);
				a.draw(g1, width, height);
				b.draw(g2, width, height);
		}
	}

	public boolean done() {
		return b.done();
	}
}