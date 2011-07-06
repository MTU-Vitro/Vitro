package demos;

import demos.lunar.*;
import vitro.*;
import vitro.plane.*;

public class LunarEye extends Host {
	private static final long serialVersionUID = 1L;

	public static final double G_MOON  = -1.624;
	public static final double G_EARTH = -9.800;
	public static final double G_MARS  = -3.710;

	public static void main(String[] args) {
		new LunarEye();
	}

	public LunarEye() {
		Lunar model           = new Lunar(-1.0, new Position(0.0, 10.0));

		Lander lander = new Lander(model);
		model.positions.put(lander, new Position(000.0, 400.0));
		lander.velocity = new Vector2(0.0, 0.0);

		Lander next = new Lander(model);
		model.positions.put(next, new Position(-200.0, 500.0));
		next.velocity = new Vector2(5.0, 0.0);

		Controller controller = new SequentialController(model);
		LunarView view        = new LunarView(model, controller, 640, 480);

		controller.bind(lander.navigation, new LunarBrain());

		show(view);
	}
}
