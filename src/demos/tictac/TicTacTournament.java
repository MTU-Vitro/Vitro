package demos.tictac;

import java.util.*;
import vitro.*;
import vitro.grid.*;
import vitro.tools.*;

public class TicTacTournament extends Tournament {
	
	private Map<TicTac.Player, Agent> teamids = new HashMap<TicTac.Player, Agent>();
	public TicTacTournament() { super(2, true); }

	@Override
	protected View match(List<Agent> competitors) {
		TicTac     model      = new TicTac(3);
		Controller controller = new SequentialController(model);

		controller.bind(model.player0, competitors.get(0));
		controller.bind(model.player1, competitors.get(1));
		
		teamids.clear();
		teamids.put(model.player0, competitors.get(0));
		teamids.put(model.player1, competitors.get(1));

		return new TicTacView(controller);
	}

	@Override
	protected Map<Agent, Integer> evaluate(View m) {
		Controller cont = m.controller();
		TicTac model = (TicTac)(cont.model());

		Map<Agent, Integer> ret = new HashMap<Agent, Integer>();
		ret.put(teamids.get(model.player0), model.hasWon(model.player0.team()) ? 1 : 0);
		ret.put(teamids.get(model.player1), model.hasWon(model.player1.team()) ? 1 : 0);
		return ret;
	}
}