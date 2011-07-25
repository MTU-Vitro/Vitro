package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import java.awt.Color;


public class SweeperEye extends Host {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new SweeperEye();	
	}

	public SweeperEye() {

		Sweeper model         = new Sweeper(20, 20, 50);
		Controller controller = new SimultaneousController(model);
		SweeperView view      = new SweeperView(model, controller, 600, 600);

		controller.bind(model.player, new SweeperAgent());
		show(view);
	}
}
