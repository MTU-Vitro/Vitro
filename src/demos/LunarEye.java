package demos;

import vitro.*;
import vitro.plane.*;

public class LunarEye extends Host {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new LunarEye();
	}

	public LunarEye() {
		LunarWorld model                = new LunarWorld();
		SequentialController controller = new SequentialController(model);
		LunarView view                  = new LunarView(model, controller, 640, 480);

		model.positions.put(model.lander, new Position(200.0, 200.0));
		controller.bind(model.lander, new LunarBrain());

		show(view);
	}
}
