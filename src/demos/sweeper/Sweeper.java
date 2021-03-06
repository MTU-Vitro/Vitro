package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class Sweeper extends Grid {
	public final Player player = new Player();

	public final Set<Location> hidden = new HashSet<Location>();
	public final Set<Location> mines  = new HashSet<Location>();
	public final Map<Location, Integer> counts = new HashMap<Location, Integer>();

	public Sweeper(int width, int height, int numMines) {
		super(width, height);
		actors.add(player);

		hidden.addAll(allCells());
		generateMines(numMines);
	}

	public void createMine(int x, int y) {
		locations.put(new Mine(), new Location(this, x, y));
	}

	public void clearSafeArea() {
		List<Location> locations = new ArrayList<Location>(allCells());
		Collections.shuffle(locations);
		Location place = null;
		for(Location location : locations) {
			if (counts.get(location) == 0) {
				place = location;
				break;
			}
		}
		if (place == null) {
			throw new Error("Unable to find an exposed safe region.");
		}
		hidden.removeAll(clear(place));
	}

	protected Set<Location> clear(Location location) {
		Set<Location> cascade = new HashSet<Location>();
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
		return cascade;
	}

	protected void generateMines(int numMines) {
		for(Actor actor : actors) {
			if(actor instanceof Mine) { actors.remove(actor); }
		}
		counts.clear();
		mines.clear();

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
			mines.add(toPlace);
		}
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Location location = new Location(model, x, y);
			
				if(Groups.containsType(Mine.class, actorsAt(location))) { 
					counts.put(location, -1);
					continue;
				}
				
				counts.put(location, Groups.ofType(Mine.class, actorsAt(neighbors(location, ADJACENT))).size());
			}
		}
	}

	public boolean done() {
		for(Location mine : mines) {
			if(!hidden.contains(mine)) { return true; }
		}
		if(mines.size() == hidden.size()) { return true; }
		
		return false;
	}
	
	public boolean success() {
		for(Location mine : mines) {
			if(!hidden.contains(mine)) { return false; }
		}
		if(mines.size() == hidden.size()) { return true; }
		
		return false;
	}

	public int count(Location location) {
		return counts.get(location);
	}


	public static class Mine extends Actor { }

	public class Player extends Actor {

		public int width()  { return model.width;  }
		public int height() { return model.height; }

		public Location createLocation(java.awt.Point point) {
			return new Location(model, point.x, point.y);
		}

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
				cascade = ((Sweeper)model).clear(location);
			}

			hidden.removeAll(cascade);
		}

		public void undo() {
			hidden.addAll(cascade);
		}
	}
}
