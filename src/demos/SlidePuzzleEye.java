package demos;

import vitro.*;
import vitro.grid.*;

public class SlidePuzzleEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new SlidePuzzleEye();
	}

	public SlidePuzzleEye() {

		SlidePuzzle model               = new SlidePuzzle(2, 2);
		SequentialController controller = new SequentialController(model);
		SlidePuzzleView view            = new SlidePuzzleView(model, controller, 640, 480, new ColorScheme());

		controller.bind(SlidePuzzle.Gap.class, new SlidePuzzleBrain());

		model.shuffle();

		show(view);
	}
}
