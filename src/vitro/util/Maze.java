package vitro.util;

import java.util.*;

public class Maze {
	
	public enum Direction { LEFT, UP, RIGHT, DOWN; }
	
	public final int width;
	public final int height;
	
	private final boolean[][][] pass;
	
	/**
	*
	**/
	public Maze(int width, int height) {
		this.width  = width;
		this.height = height;
		
		pass = new boolean[width][height][2];
	}
	
	//
	private boolean check(int x, int y) {
		return !( (x < 0) || (x >= width ) || (y < 0) || (y >= height) );
	}
	
	/**
	*
	**/
	public boolean passable(int x, int y, Direction dir) {
		if(dir == Direction.RIGHT) { return passable(x + 1, y    , Direction.LEFT); }
		if(dir == Direction.DOWN ) { return passable(x    , y + 1, Direction.UP  ); }
		
		if(check(x, y)) { return pass[x][y][dir.ordinal()]; }
		return false;
	}
	
	/**
	*
	**/
	public void setPassable(int x, int y, Direction dir, boolean v) {
		if(dir == Direction.RIGHT) { setPassable(x + 1, y    , Direction.LEFT, v); return; }
		if(dir == Direction.DOWN ) { setPassable(x    , y + 1, Direction.UP  , v); return; }
		
		if(check(x, y)) {
			if(x == 0 && dir == Direction.LEFT) { return; }
			if(y == 0 && dir == Direction.UP  ) { return; }
			
			pass[x][y][dir.ordinal()] = v;
		}
	}
	
	/**
	*
	**/
	public int[][] asIntegerGrid() {
		int[][] grid = new int[2 * height + 1][2 * width + 1];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				grid[2 * y + 1][2 * x + 1] = 1;
				
				if(passable(x, y, Direction.UP   )) { grid[2 * y    ][2 * x + 1] = 1; }
				if(passable(x, y, Direction.DOWN )) { grid[2 * y + 2][2 * x + 1] = 1; }
				if(passable(x, y, Direction.LEFT )) { grid[2 * y + 1][2 * x    ] = 1; }
				if(passable(x, y, Direction.RIGHT)) { grid[2 * y + 1][2 * x + 2] = 1; }
			}
		}
		return grid;
	}
	
	/**
	* 
	**/
	public String toString() {
		StringBuilder ret = new StringBuilder();

		for(int y = 0; y < height; y++) {			
			for(int x = 0; x < width; x++) {
				boolean w = !passable(x - 1, y    , Direction.UP  );
				boolean e = !passable(x    , y    , Direction.UP  );
				boolean n = !passable(x    , y - 1, Direction.LEFT);
				boolean s = !passable(x    , y    , Direction.LEFT);

				if      ((x == 0 && !e) || (x == width  && !w)) { ret.append('|'); }
				else if ((y == 0 && !s) || (y == height && !n)) { ret.append('-'); }
				else if ( n &&  s && !e && !w)                  { ret.append('|'); }
				else if (!n && !s &&  e &&  w)                  { ret.append('-'); }
				else if (!n && !s && !e && !w)                  { ret.append(' '); }
				else                                            { ret.append('+'); }

				ret.append(passable(x, y, Direction.UP) ? "   " : "---");
			}
			
			if (passable(width - 1, y, Direction.UP)) { ret.append("|\n"); }
			else                                      { ret.append("+\n"); }
			
			for(int x = 0; x < width; x++) {
				ret.append(passable(x, y, Direction.LEFT) ? " " : "|");

				boolean u = passable(x, y, Direction.UP  );
				boolean l = passable(x, y, Direction.LEFT);

				// ignoring symbols for now
				ret.append("   ");
			}
			ret.append("|\n");
		}
		
		for(int x = 0; x < width; x++) {
			if (passable(x, height - 1, Direction.LEFT)) {
				ret.append("----");
			}
			else {
				ret.append("+---");
			}
		}
		ret.append("+");
		
		return ret.toString();
	}
}
