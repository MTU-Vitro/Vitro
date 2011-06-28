package demos;

import vitro.*;
import vitro.plane.*;

public class BoidEye extends Host {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new BoidEye();
	}

	public BoidEye() {
		BoidWorld model                 = new BoidWorld(10.0, 10.0);
		SequentialController controller = new SequentialController(model);
		BoidView view                   = new BoidView(model, controller, 640, 480, new ColorScheme());

		for(int x = 0; x < 30; x++) {
			Position rndPos = new Position(Math.random() / 0.5 + 3.5, Math.random() / 0.5 + 2.5);
			model.positions.put(model.createBoid(), rndPos);
		}

		show(view);
	}
}
