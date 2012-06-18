package present;

import vitro.*;
import vitro.tools.*;
import demos.sweeper.*;

public class PresentationDemo extends SlideShow {

	public static void main(String[] args) {
		PresentTool.main(new PresentationDemo());
	}

	public PresentationDemo() {
		add("/home/campus06/jwearnes/Desktop/aiViz.pdf");
		addImage(1, "/home/campus06/jwearnes/Desktop/cleansweep.png");
		addImage(2, "/home/campus06/jwearnes/Desktop/Jseries");
		//add("/home/campus06/jwearnes/Desktop/chapter01.pdf");
		add("/home/campus06/jwearnes/Desktop/chapter01(2).pdf");

		Sweeper model         = new Sweeper(58, 40, 300);
		Controller controller = new SimultaneousController(model);
		SweeperView view      = new SweeperView(controller);
		controller.bind(model.player, new SweeperAgent());
		model.clearSafeArea();
		add(1, view);
	}
}