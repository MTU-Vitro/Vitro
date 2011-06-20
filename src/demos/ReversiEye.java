package demos;

import vitro.*;
import vitro.grid.*;

public class ReversiEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new ReversiEye();
	}

	public ReversiEye() {

		ReversiBoard model              = new ReversiBoard(8, 8);
		SequentialController controller = new SequentialController(model);
		ReversiView view                = new ReversiView(model, controller, 640, 480, new ColorScheme());

		ReversiBoard.Player black = model.createPlayer(ReversiBoard.BLACK);
		ReversiBoard.Player white = model.createPlayer(ReversiBoard.WHITE);

		model.actors.add(black);
		model.actors.add(white);

		controller.bind(black, new RandomAgent());
		controller.bind(white, new RandomAgent());

		show(view);
	}
}
