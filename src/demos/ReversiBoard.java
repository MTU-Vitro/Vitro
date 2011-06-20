package demos;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import static vitro.util.Groups.*;

public class ReversiBoard extends Grid implements Factional {

	public static final int WHITE = 1;
	public static final int BLACK = 2;

	protected int team = BLACK;
	public int team() {
		return team;
	}

	public Piece  createPiece(int team)  { return new Piece(team);  }
	public Player createPlayer(int team) { return new Player(team); }

	public ReversiBoard(int width, int height) {
		super(width, height);
		locations.put(new Piece(WHITE), new Location(this, width/2-1, height/2-1));
		locations.put(new Piece(BLACK), new Location(this, width/2-1, height/2  ));
		locations.put(new Piece(BLACK), new Location(this, width/2,   height/2-1));
		locations.put(new Piece(WHITE), new Location(this, width/2  , height/2  ));
	}

	public boolean done() {
		return emptyCells().size() == 0;
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
			Location oldScan = location;
			while(true) {
				Location scan = oldScan.add(delta[0],delta[1]);
				if (scan.equals(oldScan)) { break; }
				oldScan = scan;
				Piece target = (Piece)model.actorAt(scan);
				if (target == null) { break; }
				if (target.team == team) { ret.addAll(swath); break; }
				swath.add(target);
			}
		}
		return ret;
	}

	public class ReversiMove extends CreateAction {
		public final int team;
		public final Map<Piece, Integer> captured = new HashMap<Piece, Integer>();

		public ReversiMove(ReversiBoard model, Location location, Piece actor) {
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
			((ReversiBoard)model).team = (team == BLACK) ? WHITE : BLACK;			
		}

		public void undo() {
			((ReversiBoard)model).team = team;
			for(Map.Entry<Piece, Integer> entry : captured.entrySet()) {
				entry.getKey().team = entry.getValue();
			}
			super.undo();
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
			for(Location location : emptyCells()) {
				if (captured(team, location).size() > 0) {
					ret.add(new ReversiMove((ReversiBoard)model, location, new Piece(team)));
				}
			}
			return ret;
		}
	}
}