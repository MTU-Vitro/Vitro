package vitro.util;

import java.util.*;

public class MazeFactory {
	
	protected enum Delta { 
		UP    (  0, -1, Maze.Direction.UP    ),
		DOWN  (  0,  1, Maze.Direction.DOWN  ),
		LEFT  ( -1,  0, Maze.Direction.LEFT  ),
		RIGHT (  1,  0, Maze.Direction.RIGHT );
		
		public final int dx;
		public final int dy;
		public final Maze.Direction direction;
		
		private Delta(int dx, int dy, Maze.Direction direction) {
			this.dx = dx;
			this.dy = dy;
			this.direction = direction;
		}
	}
	
	protected static final List<Delta> deltas = new ArrayList<Delta>();
	{
		deltas.add(Delta.UP   );
		deltas.add(Delta.DOWN );
		deltas.add(Delta.LEFT );
		deltas.add(Delta.RIGHT);
	}
	
	public final int width;
	public final int height;
	
	public final int xStart;
	public final int yStart;
	
	public final Random random;
	
	/**
	*
	**/
	public MazeFactory(int width, int height, int xStart, int yStart, Random random) {
		this.width  = width;
		this.height = height;
		
		this.xStart = xStart;
		this.yStart = yStart;
		
		this.random = random;
	}
	
	//
	private boolean check(int x, int y) {
		return !( (x < 0) || (x >= width ) || (y < 0) || (y >= height) );
	}
	
	/**
	*
	**/
	public Maze generate() {
		final int START_X = 0;
		final int START_Y = 0;
		
		Maze maze = new Maze(width, height);
		depthFirst(maze, new boolean[height][width], xStart, yStart);
		
		return maze;
	}
	
	private void depthFirst(Maze m, boolean[][] visited, int x, int y) {
		visited[y][x] = true;
		
		List<Delta> dirs = new ArrayList<Delta>(deltas);
		Collections.shuffle(dirs, random);
		
		for(Delta delta : dirs) {
			int a = x + delta.dx;
			int b = y + delta.dy;
			
			if(!check(a, b) || visited[b][a]) { continue; }
			
			m.setPassable(x, y, delta.direction, true);
			depthFirst(m, visited, a, b);
		}
	}
}
