package demos.search;

import vitro.*;
import vitro.grid.*;


public class SearchAssignment extends Host {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		new SearchAssignment();	
	}
	
	public SearchAssignment() {
		
		boolean[][] passability = new boolean[][] {
			{ false, false, false, false, false, false, false, false },
			{ false, true , true , true , true , true , true , false },
			{ false, true , false, true , true , false, true , false },
			{ false, false, true , true , true , false, true , false },
			{ false, true , true , false, true , false, false, false },
			{ false, true , true , false, true , true , true , false },
			{ false, true , false, false, true , false, true , false },
			{ false, true , true , true , true , false, true , false },
			{ false, false, false, false, false, false, false, false },
		};
		
		Search model = new Search(passability);
		model.locations.put(model.actor , new Location(model, 1, 1));
		model.locations.put(model.target, new Location(model, 6, 7));
		
		
		Controller controller = new SimultaneousController(model);
		SearchView view       = new SearchView(model, controller, 600, 600);
		
		controller.bind(model.actor, new SearchAgent(SearchAgent.Type.DEPTH));
		controller.next();
		show(view);
	}
}
