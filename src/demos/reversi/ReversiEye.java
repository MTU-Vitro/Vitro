package demos.reversi;

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

		//model.createPiece(42, 0, 0);
		//model.createPiece(42, 0, 7);
		//model.createPiece(42, 7, 0);
		//model.createPiece(42, 7, 7);

		Reversi.Player black = model.createPlayer(Reversi.BLACK);
		Reversi.Player white = model.createPlayer(Reversi.WHITE);
		//Reversi.Player zomb = model.createPlayer(42);

		model.actors.add(black);
		model.actors.add(white);
		//model.actors.add(zomb);

		controller.bind(black, new ReversiBrain());
		controller.bind(white, new ReversiBrain());
		//controller.bind(zomb, new ReversiBrain());

		show(view);
	}
}
