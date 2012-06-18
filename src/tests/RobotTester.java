package tests;

import demos.robots.Robots;

import vitro.*;
import vitro.grid.*;
import java.util.*;

import org.junit.Test;
import static vitro.util.Groups.*;
import static org.junit.Assert.*;

public class RobotTester {

	final int s = Robots.SOLID;
	final int c = Robots.LIGHT;
	final int t = Robots.TARGET;
	final int g = Robots.SLUDGE;

	@Test
	public void testPush() {
		Robots world = new Robots(new int[][] {
			{ s, s, s, s, s },
			{ s, c, c, c, s },
			{ s, s, s, s, s }
		});
		Robots.BLU bot = world.createBLU();
		world.locations.put(world.createBlock(), new Location(world, 2, 1));
		world.locations.put(bot,                 new Location(world, 1, 1));

		assertTrue(bot.actions().size() == 1);
		assertTrue(first(bot.actions()) instanceof Robots.PushAction);
		assertTrue(((Robots.PushAction)first(bot.actions())).pushedTo.equals(new Location(world, 3, 1)));

		world.tiles[1][3] = Robots.SOLID;
		assertTrue(bot.actions().size() == 0);

		world.tiles[1][3] = Robots.LIGHT;
		world.locations.put(world.createBlock(), new Location(world, 3, 1));
		assertTrue(bot.actions().size() == 0);
	}

	@Test
	public void testPassability() {
		Robots world = new Robots(new int[][] {
			{ c, s, c },
			{ s, c, s },
			{ c, s, c }
		});
		Robots.BLU bot = world.createBLU();
		world.locations.put(bot, new Location(world, 1, 1));

		assertTrue(bot.actions().size() == 0);
		
		world.tiles[1][0] = Robots.LIGHT;

		assertTrue(bot.actions().size() == 1);
		assertTrue(first(bot.actions()) instanceof MoveAction);
		assertTrue(((MoveAction)first(bot.actions())).end.equals(new Location(world, 0, 1)));
	}

	@Test
	public void testDone() {
		Robots world = new Robots(new int[][] {
			{ c, t, t }
		});
		assertFalse(world.done());

		world.locations.put(world.createBlock(), new Location(world, 1, 0));
		assertFalse(world.done());

		Robots.Block block = world.createBlock();
		world.locations.put(block, new Location(world, 0, 0));
		assertFalse(world.done());

		world.locations.put(block, new Location(world, 2, 0));
		assertTrue(world.done());
	}

	@Test
	public void testSludge() {
		Robots world = new Robots(new int[][] {
			{ c },
			{ g }
		});
		Robots.BLU bot = world.createBLU();
		world.locations.put(bot, new Location(world, 0, 0));
		Action suicide = first(bot.actions());

		suicide.apply();
		List<Action> cleanup = world.cleanup();
		for(Action a : cleanup) { a.apply(); }
		assertFalse(world.actors.contains(bot));
		
		Collections.reverse(cleanup);
		for(Action a : cleanup) { a.undo(); }
		suicide.undo();
		assertTrue(world.actorAt(new Location(world, 0, 0)) == bot);
	}
}