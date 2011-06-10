package demos;

import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import vitro.view.*;

public class WumpusEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new WumpusEye();
	}

	public WumpusEye() {
		
		WumpusWorld model     = new WumpusWorld();
		Controller controller = new SimultaneousController(model);
		GraphView view        = new GraphView(model, controller, 800, 600);

		WumpusWorld.Hunter hunter = model.createHunter();
		controller.bind(hunter, new LogicalWumpusBrain());

		Node[][] grid = view.layoutGrid(4, 4);
		
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				view.createEdge(grid[x][y], grid[x + 1][y]);
				view.createEdge(grid[x + 1][y], grid[x][y]);
				view.createEdge(grid[x][y], grid[x][y + 1]);
				view.createEdge(grid[x][y + 1], grid[x][y]);
				/*
				if(Math.random() > 0.4) {
					if(Math.random() > 0.5) {
						view.createEdge(grid[x][y], grid[x + 1][y + 1]);
						view.createEdge(grid[x + 1][y + 1], grid[x][y]);
					}
					else {
						view.createEdge(grid[x + 1][y], grid[x][y + 1]);
						view.createEdge(grid[x][y + 1], grid[x + 1][y]);
					}
				}
				*/
			}
		}
		
		for(int z = 0; z < 3; z++) {
			view.createEdge(grid[3][z], grid[3][z + 1]);
			view.createEdge(grid[3][z + 1], grid[3][z]);
			view.createEdge(grid[z][3], grid[z + 1][3]);
			view.createEdge(grid[z + 1][3], grid[z][3]);
		}

		grid[0][0].actors.add(hunter);
		grid[3][2].actors.add(model.createWumpus());
		grid[3][0].actors.add(model.createWumpus());
		grid[3][1].actors.add(model.createPit());
		grid[2][1].actors.add(model.createPit());
		grid[0][3].actors.add(model.createBat());
		//grid[2][2].actors.add(model.createWumpus());

		view.showKey(true);
		show(view);
	}
}
