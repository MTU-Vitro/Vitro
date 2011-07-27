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
			{ 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 0, 2, 1, 0 },
			{ 0, 0, 1, 0, 1, 1, 0 },
			{ 0, 0, 2, 0, 1, 0, 0 },
			{ 0, 0, 1, 1, 1, 3, 0 },
			{ 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0 }
		});
		model.locations.put(model.createRNG(),   new Location(model, 2, 1));
		model.locations.put(model.createBLU(),   new Location(model, 2, 2));
		model.locations.put(model.createBLU(),   new Location(model, 4, 1));
		model.locations.put(model.createBlock(), new Location(model, 4, 2));

		/*
		// a big maze
		Robots model = new Robots(new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0 },
			{ 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0 },
			{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
			{ 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0 },
			{ 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0 },
			{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0 },
			{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 0, 3, 1, 1, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		});
		model.locations.put(model.createBLU(),   new Location(model, 1, 1));
		//model.locations.put(model.createRNG(),   new Location(model, 1, 6));
		//model.locations.put(model.createRNG(),   new Location(model, 5, 1));
		//model.locations.put(model.createRNG(),   new Location(model, 4, 3));
		*/

		/*
		// basic testing of RNG dragging stuff
		Robots model = new Robots(new int[][] {
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 1, 3, 0 },
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0 }
		});
		model.locations.put(model.createRNG(),   new Location(model, 1, 2));
		model.locations.put(model.createBlock(), new Location(model, 2, 2));
		*/

		SequentialController controller = new SequentialController(model);
		RobotsView view                 = new RobotsView(model, controller);

		controller.bind(Robots.BLU.class, new RandomAgent());
		controller.bind(Robots.RNG.class, new RandomAgent());
		show(view);
	}
}