package present;

import java.awt.Color;
import vitro.*;
import vitro.tools.*;
import demos.slide.*;
import demos.sweeper.*;

public class PresentationDemo extends SlideShow {

	public static void main(String[] args) {
		PresentTool.main(new PresentationDemo());
	}

	public PresentationDemo() {
		final String basePath = "/Users/rodger/Desktop/Slides/";

		ColorScheme onBlack = new ColorScheme(
			Color.WHITE, // outline
			Color.GRAY,  // secondary
			Color.BLACK  // background
		);

		addImage(basePath + "slide0.png");  // title page
		addImage(basePath + "slide1.png");  // motivation
		addImage(basePath + "slide2.png");  // vitro
		addImage(basePath + "slide3.png");  // design
		addImage(basePath + "slide4.png");  // vacuum world
		addImage(basePath + "slide5.png");  //   state
		addImage(basePath + "slide6.png");  //   change
		addImage(basePath + "slide7.png");  //   presentation

		{ // slide puzzle demo:
			SlidePuzzle model = new SlidePuzzle(3, 3);
			model.shuffle();
			SequentialController controller = new SequentialController(model);
			controller.bind(SlidePuzzle.Gap.class, new SlidePuzzleBrain());
			SlidePuzzleView view = new SlidePuzzleView(controller, 640, 480, onBlack);
			add(view);
		}

		addImage(basePath + "slide8.png");  // usage
		addImage(basePath + "slide9.png");  // model assignment: reversi
		addImage(basePath + "slide10.png"); // more reversi
		addImage(basePath + "slide11.png"); // usage results
		addImage(basePath + "slide12.png"); // future directions
		addImage(basePath + "slide13.png");
		addImage(basePath + "slide14.png");
		
		{ // lunar lander demo:
			
		}

		addImage(basePath + "slide15.png"); // questions?
		addImage(basePath + "slide16.png"); // references

		{ // minesweeper demo:
			Sweeper model         = new Sweeper(58, 40, 300);
			Controller controller = new SimultaneousController(model);
			SweeperView view      = new SweeperView(controller);
			controller.bind(model.player, new SweeperAgent());
			model.clearSafeArea();
			add(view);
		}
	}
}