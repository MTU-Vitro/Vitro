package demos.lunar;

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
		Lunar model = new Lunar(-1.0, new Position(Math.random() * 400 - 200, 10.0));

		Lander lander = new Lander(model);
		model.positions.put(lander, new Position(Math.random() * 400 - 200, 400.0));
		//lander.velocity = new Vector2(Math.random() * 20 - 10, 0.0);

		Controller controller = new SequentialController(model);
		LunarView view        = new LunarView(model, controller, 640, 480);

		controller.bind(lander.navigation, new LunarBrain());

		show(view);
	}
}
