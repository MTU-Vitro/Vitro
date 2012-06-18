package demos.tictac;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class TicTac extends Grid implements Factional {
	public final int size;

	public static final int CROSSES = 0;
	public static final int CIRCLES = 1;
	
	protected int team = CROSSES;
	public int team() {
		return team;
	}
	
	private int nextTeam() {
		return (team + 1) % 2;
	}

	public Player player0 = new Player(CROSSES);
	public Player player1 = new Player(CIRCLES);

	public TicTac(int size) {
		super(size, size);
		this.size = size;
		
		actors.add(player0);
		actors.add(player1);
	}

	public boolean hasWon(int teamVal) {
		for(int x = 0; x < size; x++) {
			int count = 0;
			for(int y = 0; y < size; y++) {
				Actor actor = actorAt(new Location(this, x, y));
				
				if(actor == null)                    { break;   }
				if(((Piece)actor).team() == teamVal) { count++; }
			}
			if(count == size) { return true; }
		}
		
		
		for(int y = 0; y < size; y++) {
			int count = 0;
			for(int x = 0; x < size; x++) {
				Actor actor = actorAt(new Location(this, x, y));
				
				if(actor == null)                    { break;   }
				if(((Piece)actor).team() == teamVal) { count++; }
			}
			if(count == size) { return true; }
		}
		
		{
			int count = 0;
			for(int s = 0; s < size; s++) {
				Actor actor = actorAt(new Location(this, s, s));
				
				if(actor == null)                    { break;   }
				if(((Piece)actor).team() == teamVal) { count++; }
			}
			if(count == size) { return true; }
		}
		
		{
			int count = 0;
			for(int s = 0; s < size; s++) {
				Actor actor = actorAt(new Location(this, size - s - 1, s));
				
				if(actor == null)                    { break;   }
				if(((Piece)actor).team() == teamVal) { count++; }
			}
			if(count == size) { return true; }
		}
		
		return false;
	}

	public boolean done() {
		if(hasWon(nextTeam())) { return true; } // only the previous (next) team could have won.
		if(Groups.ofType(Piece.class, actors).size() == size * size) { return true; }
	
		return false;
	}
	
	public class Piece extends Actor implements Factional {
		public final int team;
		
		public Piece(int team) {
			this.team = team;
		}
		
		public int team() {
			return team;
		}
	}
	
	public class Move extends CreateAction {
		public final int team;
		
		public Move(TicTac model, Location location, Piece actor) {
			super(model, location, actor);
			this.team = actor.team;
		}
		
		public void apply() {
			super.apply();
			((TicTac)model).team = nextTeam();
		}
		
		public void undo() {
			((TicTac)model).team = team;
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
				ret.add(new Move((TicTac)model, location, new Piece(team)));
			}
			return ret;
		}
	}
}
