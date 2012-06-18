package demos.RL;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class RLEye extends Host {

	public static void main(String[] args) {
		new RLEye();
	}

	public RLEye() {
		RLModel model = new RLModel(8, 1, 0, 1,
			new int[][] {
				{1, 1, 1, 2},
				{1, 0, 1, 2},
				{1, 1, 1, 1}
			},
			new int[][] {
				{-1,-1,-1, 10},
				{-1, 0,-1,-10},
				{-1,-1,-1, -1}
			}
		);
		SequentialController controller = new SequentialController(model);

		model.startPosition(0, 2);
		controller.bind(model.actor(), new RLBrain());

		LoopController loop = new LoopController(controller, 10);
		RLView view         = new RLView(loop, 480, 480);
		show(view);
	}
}