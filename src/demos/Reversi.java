package demos;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import static vitro.util.Groups.*;

public class Reversi extends Grid implements Factional {

	public static final int BLACK = 1;
	public static final int WHITE = 2;

	protected int team = BLACK;
	public int team() {
		return team;
	}

	public Piece  createPiece(int team)  { return new Piece(team);  }
	public Player createPlayer(int team) { return new Player(team); }

	public Reversi(int width, int height) {
		super(width, height);
		locations.put(new Piece(WHITE), new Location(this, width/2-1, height/2-1));
		locations.put(new Piece(BLACK), new Location(this, width/2-1, height/2  ));
		locations.put(new Piece(BLACK), new Location(this, width/2,   height/2-1));
		locations.put(new Piece(WHITE), new Location(this, width/2  , height/2  ));
	}

	public boolean done() {
		if (emptyCells().size() == 0) { return true; }
		for(Actor a : ofType(Player.class, actors)) {
			if (a.actions().size() > 1) { return false; }
		}
		return true;
	}

	public class Piece extends Actor implements Factional {
		public int team;
		public Piece(int team) {
			this.team = team;
		}

		public int team() {
			return team;
		}
	}

	private Set<Piece> captured(int team, Location location) {
		Set<Piece> ret = new HashSet<Piece>();
		if (actorAt(location) != null) { return ret; }
		for(int[] delta : ADJACENT) {
			Set<Piece> swath = new HashSet<Piece>();
			Location scan = location;
			while(true) {
				scan = scan.add(delta[0],delta[1]);
				if (!scan.valid()) { break; }
				Piece target = (Piece)model.actorAt(scan);
				if (target == null) { break; }
				if (target.team == team) { ret.addAll(swath); break; }
				swath.add(target);
			}
		}
		return ret;
	}

	private void nextTeam() {
		team = (team == BLACK) ? WHITE : BLACK;
	}

	public class Move extends CreateAction {
		public final int team;
		public final Map<Piece, Integer> captured = new HashMap<Piece, Integer>();

		public Move(Reversi model, Location location, Piece actor) {
			super(model, location, actor);
			this.team = actor.team;
			for(Piece piece : captured(team, location)) {
				captured.put(piece, piece.team);
			}
		}

		public void apply() {
			super.apply();
			for(Piece piece : captured.keySet()) {
				piece.team = team;
			}
			((Reversi)model).nextTeam();
		}

		public void undo() {
			((Reversi)model).team = team;
			for(Map.Entry<Piece, Integer> entry : captured.entrySet()) {
				entry.getKey().team = entry.getValue();
			}
			super.undo();
		}
	}

	public class Pass implements Action {
		public final Reversi model;
		public final int team;

		public Pass(Reversi model, int team) {
			this.model = model;
			this.team = team;
		}

		public void apply() {
			model.nextTeam();
			System.out.println("Player "+team+" passed their turn.");
		}

		public void undo() {
			model.team = team;
		}
	}

	public class Player extends Actor implements Factional {
		private final int team;

		public Player(int team) {
			this.team = team;
		}

		public int team() {
			return team;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			ret.add(new Pass((Reversi)model, team));
			for(Location location : emptyCells()) {
				if (captured(team, location).size() > 0) {
					ret.add(new Move((Reversi)model, location, new Piece(team)));
				}
			}
			return ret;
		}
	}
}