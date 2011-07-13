package demos.robots;

import vitro.*;
import vitro.grid.*;

public class RobotsEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new RobotsEye();
	}

	public RobotsEye() {

		Robots model = new Robots(new int[][] {
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 2, 1, 0 },
			{ 0, 2, 0, 1, 1, 0 },
			{ 0, 2, 0, 1, 0, 0 },
			{ 0, 1, 1, 1, 3, 0 },
			{ 0, 0, 0, 0, 0, 0 }
		});

		SequentialController controller = new SequentialController(model);
		RobotsView view                 = new RobotsView(model, controller);

		controller.bind(Robots.BLU.class, new RandomAgent());

		model.locations.put(model.createBLU(),   new Location(model, 1, 1));
		model.locations.put(model.createBLU(),   new Location(model, 3, 1));
		model.locations.put(model.createBlock(), new Location(model, 3, 2));
		model.locations.put(model.createBLU(),   new Location(model, 3, 3));

		show(view);
	}
}