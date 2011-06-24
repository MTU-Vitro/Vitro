package demos;

import vitro.*;
import vitro.grid.*;

public class ReversiEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ReversiEye();
	}

	public ReversiEye() {

		Reversi model                   = new Reversi(8, 8);
		SequentialController controller = new SequentialController(model);
		ReversiView view                = new ReversiView(model, controller, 640, 480, new ColorScheme());

		Reversi.Player black = model.createPlayer(Reversi.BLACK);
		Reversi.Player white = model.createPlayer(Reversi.WHITE);

		model.actors.add(black);
		model.actors.add(white);

		controller.bind(black, new ReversiBrain());
		controller.bind(white, new RandomAgent());

		show(view);
	}
}
