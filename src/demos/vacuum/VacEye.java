package demos.vacuum;

import vitro.*;
import vitro.graph.*;
import vitro.util.*;
import java.util.*;

public class VacEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new VacEye();
	}

	public VacEye() {

		VacWorld model                  = new VacWorld();
		SequentialController controller = new SequentialController(model);
		GraphView view                  = new GraphView(controller, 720, 720);

		controller.bind(VacWorld.Scrubby.class, new VacBrain());

		Node entrance    = view.createNode(.05, .58, "Entrance"    );
		Node hallway0    = view.createNode(.15, .58, "Hallway"     );
		Node hallway1    = view.createNode(.40, .58, "Hallway"     );
		Node hallway2    = view.createNode(.65, .58, "Hallway"     );
		Node hallway3    = view.createNode(.90, .58, "Hallway"     );
		Node living0     = view.createNode(.15, .74, "Living Room" );
		Node living1     = view.createNode(.40, .74, "Living Room" );
		Node living2     = view.createNode(.65, .74, "Living Room" );
		Node living3     = view.createNode(.15, .90, "Living Room" );
		Node living4     = view.createNode(.40, .90, "Living Room" );
		Node living5     = view.createNode(.65, .90, "Living Room" );
		Node bathroom0   = view.createNode(.90, .90, "Bathroom"    );
		Node bathroom1   = view.createNode(.90, .74, "Bathroom"    );
		Node kitchen0    = view.createNode(.15, .42, "Kitchen"     );
		Node kitchen1    = view.createNode(.40, .42, "Kitchen"     );
		Node kitchen2    = view.createNode(.15, .26, "Kitchen"     );
		Node kitchen3    = view.createNode(.40, .26, "Kitchen"     );
		Node bedroom0    = view.createNode(.65, .42, "Bedroom"     );
		Node bedroom1    = view.createNode(.90, .42, "Bedroom"     );
		Node bedroom2    = view.createNode(.65, .26, "Bedroom"     );
		Node bedroom3    = view.createNode(.90, .26, "Bedroom"     );
		Node bedroom4    = view.createNode(.65, .10, "Bedroom"     );
		Node bedroom5    = view.createNode(.90, .10, "Bedroom"     );
		Node closet0     = view.createNode(.15, .10, "Closet"      );
		Node closet1     = view.createNode(.40, .10, "Closet"      );

		createDense(view, closet0, closet1);
		createDense(view, kitchen0, kitchen1, kitchen2, kitchen3);
		//createDense(view, bedroom0, bedroom1, bedroom2, bedroom3);
		//createDense(view, bedroom2, bedroom3, bedroom4, bedroom5);
		createDense(view, bedroom0, bedroom1, bedroom2, bedroom3, bedroom4, bedroom5);
		//createDense(view, living0, living1, living3, living4);
		//createDense(view, living1, living4, living2, living5);
		createDense(view, living0, living1, living2, living3, living4, living5);
		createDense(view, bathroom0, bathroom1);
		createDense(view, entrance, hallway0);
		createDense(view, hallway0, hallway1);
		createDense(view, hallway1, hallway2);
		createDense(view, hallway2, hallway3);
		
		createDense(view, bedroom4 , closet1 );
		createDense(view, bathroom1, hallway3);
		createDense(view, bedroom1 , hallway3);
		createDense(view, living1  , hallway1);
		createDense(view, kitchen1 , hallway1);
		
		for(int x = 8; x > 0; x--) {
			model.nodes.get(x).actors.add(model.createDirt());
		}
		
		for(int x = model.nodes.size() - 1; x > 0; x--) {
			int r = (int)(Math.random() * (x + 1));
			
			List<Actor> xDirt = Groups.ofType(VacWorld.Dirt.class, model.nodes.get(x).actors);
			List<Actor> rDirt = Groups.ofType(VacWorld.Dirt.class, model.nodes.get(r).actors);
			
			model.nodes.get(x).actors.removeAll(xDirt);
			model.nodes.get(r).actors.removeAll(rDirt);
		
			model.nodes.get(x).actors.addAll(rDirt);
			model.nodes.get(r).actors.addAll(xDirt);
		}
		
		closet0.actors.add(model.createScrubby());

		/*
		Node start = view.createNode(.5, .2, "Start");
		Node roomA = view.createNode(.2, .5, "Room A");
		Node roomB = view.createNode(.4, .5, "Room B");
		Node roomC = view.createNode(.6, .5, "Room C");
		Node roomE = view.createNode(.5, .8, "End");
		Node roomF = view.createNode(.8, .5, "Loopback");

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

		start.actors.add(model.createScrubby());
		roomA.actors.add(model.createDirt());
		roomB.actors.add(model.createDirt());
		roomC.actors.add(model.createDirt());
		*/

		dockedController(true);
		show(view);
	}
	
	public void createDense(GraphView view, Node... nodes) {
		for(int x = 0; x < nodes.length; x++) {
			for(int y = 0; y < nodes.length; y++) {
				if(x != y) { view.createEdge(nodes[x], nodes[y]); }
			}
		}
	}
}
