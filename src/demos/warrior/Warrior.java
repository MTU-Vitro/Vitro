package demos.warrior;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

import static vitro.util.Groups.*;

public class Warrior extends Grid {

	public static final int SOLID = 0;
	public static final int FLOOR = 1;
	public static final int START = 2;
	public static final int SLIME = 3;
	public static final int STAIR = 4;
	public static final int GEM   = 5;
	public static final int DOOR  = 6;
	public static final int KEY   = 7;

	enum Tile      { Solid, Floor, Slime, Stair, Gem };
	enum Direction {
		North( 0, -1),
		East ( 1,  0),
		South( 0,  1),
		West (-1,  0);

		public final int deltaX;
		public final int deltaY;
		Direction(int deltaX, int deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}
	};

	public final int[][] world;

	public Warrior(int[][] world) {
		super(world[0].length, world.length);
		this.world = world;
		for(int y = 0; y < world.length; y++) {
			for(int x = 0; x < world[0].length; x++) {
				if (world[y][x] == START) { put(new Hero(this),  x, y); }
				if (world[y][x] == GEM)   { put(new Gem(),       x, y); }
				if (world[y][x] == SLIME) { put(new Slime(this), x, y); }
			}
		}
	}

	public boolean done() {
		for(Actor a : ofType(Hero.class, actors)) {
			Location loc = locations.get(a);
			if (world[loc.y][loc.x] != STAIR) { return false; }
		}
		return true;
	}

	public Location relativePosition(GridActor a, Direction dir) {
		return a.location().add(dir.deltaX, dir.deltaY);
	}

	public boolean passable(Actor actor, Location location) {
		if (!location.valid())                             { return false; }
		if (world[location.y][location.x] == SOLID)        { return false; }
		if (containsType(Slime.class, actorsAt(location))) { return false; }
		if (containsType(Hero.class,  actorsAt(location))) { return false; }
		return true;
	}

	public class AttackAction extends DestroyAction {
		public final Direction direction;
		public final Hero hero;

		AttackAction(Grid model, Hero hero, Direction direction) {
			super(model, firstOfType(Slime.class, model.actorsAt(relativePosition(hero, direction))));
			this.direction = direction;
			this.hero      = hero;
		}

		public void apply() {
			super.apply();
			hero.health--;
		}
		public void undo() {
			hero.health++;
			super.undo();
		}
	}

	public class TakeAction extends DestroyAction {
		public final Direction direction;
		public final Hero hero;

		TakeAction(Grid model, Hero hero, Direction direction) {
			super(model, firstOfType(Gem.class, model.actorsAt(relativePosition(hero, direction))));
			this.direction = direction;
			this.hero      = hero;
		}

		public void apply() {
			super.apply();
			hero.gems++;
		}
		public void undo()  {
			hero.gems--;
			super.undo();
		}
	}

	public class WalkAction extends MoveAction {
		public final Direction direction;
		public final Hero hero;

		WalkAction(Grid model, Hero hero, Direction direction) {
			super(model, relativePosition(hero, direction), hero);
			this.direction = direction;
			this.hero      = hero;
		}

		public void apply() {
			super.apply();
			hero.steps++;
		}

		public void undo() {
			hero.steps--;
			super.undo();
		}
	}

	public class Gem extends Actor {
		// gems have no significant behaviors.
	}

	public class Slime extends GridActor {
		public Slime(Warrior model) {
			super(model);
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			throw new Error("Not implemented!");
			//return ret;
		}
	}

	public class Hero extends GridActor {
		public  int steps  = 0;
		public  int health = 0;
		public  int gems   = 0;
		private final Warrior model;

		public Hero(Warrior model) {
			super(model);
			this.model = model;
		}

		public int health() {
			return health;
		}

		public Tile look(Direction dir) {
			Location p = relativePosition(this, dir);
			if (!p.valid())               { return Tile.Solid; }
			Actor pa = model.actorAt(p);
			if (pa instanceof Gem)        { return Tile.Gem;   }
			if (pa instanceof Slime)      { return Tile.Slime; }
			if (world[p.y][p.x] == SOLID) { return Tile.Solid; }
			if (world[p.y][p.x] == FLOOR) { return Tile.Floor; }
			if (world[p.y][p.x] == START) { return Tile.Floor; }
			if (world[p.y][p.x] == STAIR) { return Tile.Stair; }
			
			throw new Error("Unknown tile type!");
		}

		public int listen() {
			return ofType(Slime.class, model.actors).size();
		}

		public int smell() {
			throw new Error("Not implemented!");
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Direction dir : Direction.values()) {
				Location p = relativePosition(this, dir);
				if (!p.valid()) { continue; }
				if (passable(this, p)) {
					ret.add(new WalkAction(model, this, dir));
				}
				if (containsType(Gem.class,   actorsAt(p))) {
					ret.add(new TakeAction(model, this, dir));
				}
				if (containsType(Slime.class, actorsAt(p))) {
					ret.add(new AttackAction(model, this, dir));
				}
			}
			return ret;
		}
	}
}

// ----------------------------------

class HeroAgentA implements Agent<Warrior.Hero> {
	private final HeroBrainA brain;

	private enum Choice { NoChoice, Attack, Take, Walk };
	private Choice actionChoice;
	private Warrior.Direction actionDir;
	private Warrior.Hero hero;

	public HeroAgentA(HeroBrainA brain) {
		this.brain = brain;
		brain.setAgent(this);
	}

	Warrior.Tile look(Warrior.Direction dir) {
		return hero.look(dir);
	}
	int health() {
		return hero.health();
	}
	int listen() {
		return hero.listen();
	}
	int smell() {
		return hero.smell();
	}
	void attack(Warrior.Direction dir) {
		actionChoice = Choice.Attack;
		actionDir = dir;
	}
	void take(Warrior.Direction dir) {
		actionChoice = Choice.Take;
		actionDir = dir;
	}
	void walk(Warrior.Direction dir) {
		actionChoice = Choice.Walk;
		actionDir = dir;
	}

	public Action choose(Warrior.Hero hero, Set<Action> options) {
		this.hero = hero;
		brain.tick();
		if (actionChoice == Choice.Attack) {
			for (Action action : ofType(Warrior.AttackAction.class, options)) {
				if (((Warrior.AttackAction)action).direction == actionDir) { return action; }
			}
			actionChoice = Choice.NoChoice;
		}
		else if (actionChoice == Choice.Take) {
			for (Action action : ofType(Warrior.TakeAction.class, options)) {
				if (((Warrior.TakeAction)action).direction == actionDir) { return action; }
			}
			actionChoice = Choice.NoChoice;
		}
		else if (actionChoice == Choice.Walk) {
			for (Action action : ofType(Warrior.WalkAction.class, options)) {
				if (((Warrior.WalkAction)action).direction == actionDir) { return action; }
			}
			actionChoice = Choice.NoChoice;
		}
		return null;
	}
}

abstract class HeroBrainA {

	protected HeroAgentA agent;

	void setAgent(HeroAgentA agent) {
		this.agent = agent;
	}

	public abstract void tick();
	public abstract void level(int levelNumber);

	public Warrior.Tile look(Warrior.Direction dir) { return agent.look(dir); }
	public int listen()                             { return agent.listen();  }
	public int smell()                              { return agent.smell();   }
	public void attack(Warrior.Direction dir)       { agent.attack(dir);      }
	public void take(Warrior.Direction dir)         { agent.take(dir);        }
	public void walk(Warrior.Direction dir)         { agent.walk(dir);        }
}

// ----------------------------------

class HeroAgentB implements Agent<Warrior.Hero> {
	private final HeroBrainB brain;
	private final Map<Warrior.Hero, Integer> ids = new HashMap<Warrior.Hero, Integer>();
	private int idCounter = 0;

	public HeroAgentB(HeroBrainB brain) {
		this.brain = brain;
	}

	public Action choose(Warrior.Hero hero, Set<Action> options) {
		Set<HeroAction> actions = new HashSet<HeroAction>();
		Map<HeroAction, Action> lookup = new HashMap<HeroAction, Action>();

		for(Action a : options) {
			if (a instanceof Warrior.AttackAction) {
				HeroAction b = new AttackAction(((Warrior.AttackAction)a).direction);
				lookup.put(b, a);
				actions.add(b);
			}
			if (a instanceof Warrior.TakeAction) {
				HeroAction b = new TakeAction(((Warrior.TakeAction)a).direction);
				lookup.put(b, a);
				actions.add(b);
			}
			if (a instanceof Warrior.WalkAction) {
				HeroAction b = new WalkAction(((Warrior.WalkAction)a).direction);
				lookup.put(b, a);
				actions.add(b);
			}
		}

		HeroAction action = brain.tick(
			new Hero(
				getId(hero),
				hero.health(),
				hero.listen(),
				hero.smell()
			),
			Collections.unmodifiableSet(actions)
		);

		if (action == null)            { return null; }
		if (!actions.contains(action)) { throw new Error("Shenanigans detected!"); }
		return lookup.get(action);
	}

	private int getId(Warrior.Hero hero) {
		if (!ids.containsKey(hero)) {
			ids.put(hero, idCounter++);
		}
		return ids.get(hero);
	}
}

final class Hero {
	public final int id;
	public final int health;
	public final int listen;
	public final int smell;

	public Hero(int id, int health, int listen, int smell) {
		this.id     = id;
		this.health = health;
		this.listen = listen;
		this.smell  = smell;
	}
}

abstract class HeroAction {
	public final Warrior.Direction direction;
	public HeroAction(Warrior.Direction direction) { this.direction = direction; }
}

final class AttackAction extends HeroAction { public AttackAction(Warrior.Direction dir) { super(dir); }}
final class TakeAction   extends HeroAction { public   TakeAction(Warrior.Direction dir) { super(dir); }}
final class WalkAction   extends HeroAction { public   WalkAction(Warrior.Direction dir) { super(dir); }}

abstract class HeroBrainB {
	public abstract void level(int levelNumber);
	public abstract HeroAction tick(Hero hero, Set<HeroAction> options);
}
