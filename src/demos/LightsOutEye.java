package demos;

import vitro.*;
import vitro.grid.*;

public class LightsOutEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new LightsOutEye();
	}

	public LightsOutEye() {

		LightsOut model                 = new LightsOut(8, 8);
		SequentialController controller = new SequentialController(model);
		LightsOutView view              = new LightsOutView(model, controller, 640, 480);

		controller.bind(LightsOut.Player.class, new RandomAgent());

		model.shuffle();
		show(view);
	}
}
