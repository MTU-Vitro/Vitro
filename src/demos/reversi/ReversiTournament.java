package demos.reversi;

import java.util.*;
import vitro.*;
import vitro.grid.*;
import vitro.tools.*;

public class ReversiTournament extends Tournament {
	
	private Map<Integer, Agent> teamids = new HashMap<Integer, Agent>();
	public ReversiTournament() { super(2, true); } // tournament
	//public ReversiTournament() { super(2, 3); } // round robin

	@Override
	protected View match(List<Agent> competitors) {
		Reversi model                   = new Reversi(8, 8);
		SequentialController controller = new SequentialController(model);
		teamids.clear();

		Reversi.Player black = model.createPlayer(Reversi.BLACK);
		model.actors.add(black);
		teamids.put(Reversi.BLACK, competitors.get(0));
		controller.bind(black, competitors.get(0));

		Reversi.Player white = model.createPlayer(Reversi.WHITE);
		model.actors.add(white);
		teamids.put(Reversi.WHITE, competitors.get(1));
		controller.bind(white, competitors.get(1));

		return new ReversiView(controller, 640, 480, new ColorScheme());
	}

	@Override
	protected Map<Agent, Integer> evaluate(View m) {
		Controller cont = m.controller();
		Reversi model = (Reversi)(cont.model());

		Map<Agent, Integer> ret = new HashMap<Agent, Integer>();
		for(Map.Entry<Integer, Integer> e : model.scores().entrySet()) {
			ret.put(
				teamids.get(e.getKey()),
				e.getValue()
			);
		}
		return ret;
	}
}