package demos.wumpus;

import vitro.*;
import vitro.grid.*;

public class WumpusAIMA extends Host {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		new WumpusAIMA();
	}

	public WumpusAIMA() {
		WumpusGrid     model      = new WumpusGrid(5, 5);
		Controller     controller = new SimultaneousController(model);
		WumpusGridView view       = new WumpusGridView(model, controller, 800, 600);
		
		WumpusGrid.Hunter hunter = model.createHunter(0, 3);
		WumpusGrid.Wumpus wumpus = model.createWumpus(4, 4);
		
		//controller.bind(hunter, new RandomAgent());
		controller.bind(hunter, new LogicalWumpusGridBrain(model));
		
		show(view);
	}
}
