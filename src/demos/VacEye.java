package demos;

import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import vitro.view.*;

public class VacEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new VacEye();
	}

	public VacEye() {
		
		VacWorld model                  = new VacWorld();
		SequentialController controller = new SequentialController(model);
		GraphView view                  = new GraphView(model, controller, 640, 480);

		VacWorld.Scrubby scrubby = model.createScrubby();
		controller.bind(scrubby, new VacBrain());

		Node start = view.createNode(.5, .2, "Start");
		Node roomA = view.createNode(.3, .5, "Room A");
		Node roomB = view.createNode(.5, .5, "Room B");
		Node roomC = view.createNode(.7, .5, "Room C");
		Node roomE = view.createNode(.5, .8, "End");
		Node roomF = view.createNode(.1, .5, "");

		view.createEdge(start, roomA);
		view.createEdge(start, roomB);
		view.createEdge(start, roomC);
		view.createEdge(roomA, roomE);
		view.createEdge(roomB, roomE);
		view.createEdge(roomC, roomE);
		view.createEdge(roomE, roomF);
		view.createEdge(roomF, roomE);
		view.createEdge(roomF, start);
		view.createEdge(start, roomF);

		start.actors.add(scrubby);
		roomA.actors.add(model.createDirt());
		roomB.actors.add(model.createDirt());
		roomC.actors.add(model.createDirt());

		show(view);
	}
}
