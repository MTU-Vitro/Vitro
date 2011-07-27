package demos.tictac;

import vitro.*;
import vitro.grid.*;

public class TicTacEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new TicTacEye();
	}

	public TicTacEye() {

		TicTac     model      = new TicTac(3);
		Controller controller = new SequentialController(model);
		GridView   view       = new GridView(model, controller, 640, 480, new ColorScheme());

		//controller.bind(model.player0, new RandomAgent());
		//controller.bind(model.player1, new RandomAgent());

		show(view);
	}
}
