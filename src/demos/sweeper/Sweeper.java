package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class Sweeper extends Grid {
	public final Player player = new Player();

	public final Set<Location> hidden = new HashSet<Location>();

	public Sweeper(int width, int height, int numMines) {
		super(width, height);
		actors.add(player);

		hidden.addAll(allCells());
		generateMines(numMines);
	}

	protected void generateMines(int numMines) {
		for(Actor actor : actors) {
			if(actor instanceof Mine) { actors.remove(actor); }
		}

		Random rnd = new Random();
		for(int m = 0; m < numMines; m++) {
			int pos = rnd.nextInt(hidden.size() - m);

			Iterator<Location> iter = hidden.iterator();
			for(int l = 0; l < pos - 1; l++) {
				iter.next();
			}

			Location toPlace = iter.next();
			while(Groups.containsType(Mine.class, actorsAt(toPlace))) {
				toPlace = iter.next();
			}

			locations.put(new Mine(), toPlace);
		}
	}

	public boolean done() {
		for(Actor actor : Groups.ofType(Mine.class, actors)) {
			if(locations.get(actor) != null && !hidden.contains(locations.get(actor))) { return true; }
		}
		if(Groups.ofType(Mine.class, actors).size() == hidden.size()) { return true; } 

		return false;
	}
	
	public boolean success() {
		for(Actor actor : Groups.ofType(Mine.class, actors)) {
			if(locations.get(actor) != null && !hidden.contains(locations.get(actor))) { return false; }
		}
		if(Groups.ofType(Mine.class, actors).size() == hidden.size()) { return true; } 

		return false;
	}

	public int count(Location location) {
		if(Groups.containsType(Mine.class, actorsAt(location))) { return -1; }
		return Groups.ofType(Mine.class, actorsAt(neighbors(location, ADJACENT))).size();
	}


	public class Mine extends Actor { }

	public class Player extends Actor {

		public int width()  { return model.width;  }
		public int height() { return model.height; }

		public boolean hidden(int x, int y) {
			return hidden.contains(new Location(model, x, y));
		}

		public int count(int x, int y) {
			Location location = new Location(model, x, y);
			if (hidden(x, y)) { return 0; }
			return ((Sweeper)model).count(location);
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Location location : hidden) {
				ret.add(new FlipAction(location));
			}
			return ret;
		}
	}

	public class FlipAction implements Action {
		public final Location location;

		private Set<Location> cascade = null;

		public FlipAction(Location location) {
			this.location = location;
		}

		public void apply() {
			if(cascade == null) {
				cascade = new HashSet<Location>();
				cascade.add(location);

				// bfs search for zero count squares
				if(count(location) == 0) {
					Queue<Location> frontier = new LinkedList<Location>();
					frontier.add(location);

					Set<Location> visited = new HashSet<Location>();
					visited.add(location);

					while(!frontier.isEmpty()) {
						Location current = frontier.remove();
						for(Location next : neighbors(current, ADJACENT)) {
							if(!visited.contains(next) && count(next) == 0) {
								frontier.add(next);
							}
							visited.add(next);
						}
					}
					cascade.addAll(visited);
				}
			}

			hidden.removeAll(cascade);
		}

		public void undo() {
			hidden.addAll(cascade);
		}
	}
}
