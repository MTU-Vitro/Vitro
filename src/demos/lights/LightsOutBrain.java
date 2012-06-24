package demos.lights;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

/**
* The approach taken here is that of setting up and solving an inverse
* linear algebra problem. Currently this approach is only capable solving
* boards of size n x n, where n is odd, although I am unsure at this point
* if this is a limitation of the approach or a mistake in my implementation.
**/
public class LightsOutBrain implements Agent<LightsOut.Player> {

	Set<Location> presses = null;

	public Action choose(LightsOut.Player actor, Set<Action> options) {
		if(presses == null) {
			presses = new HashSet<Location>();

			// linear algebra solution
			boolean[][] state = actor.state();

			boolean[]   s = stateVector(state);
			boolean[][] M = stateMatrix(state[0].length, state.length);
			gauss(M, s);

			// translate to locations
			Location[][] locations = actor.locations();
			for(int x = 0; x < s.length; x++) {
				if(s[x]) { presses.add(locations[x / state.length][x % state[0].length]); }
			}
		}

		Location next = Groups.first(presses);
		presses.remove(next);

		for(Action action : Groups.ofType(LightsOut.Move.class, options)) {
			if(((LightsOut.Move)action).target.location().equals(next)) { return action; }
		}

		return null;
	}

	//
	private boolean[] stateVector(boolean[][] state) {
		boolean[] s = new boolean[state.length * state[0].length];
		for(int y = 0; y < state.length; y++) {
			for(int x = 0; x < state[0].length; x++) {
				s[state.length * y + x] = state[y][x];
			}
		}
		return s;
	}

	//
	private boolean[][] stateMatrix(int width, int height) {
		boolean[][] M = new boolean[width * height][];
		for(int s = 0; s < width * height; s++) {
			M[s] = effectVector(s % width, s / width, width, height);
		}
		return M;
	}

	private boolean[] effectVector(int x, int y, int width, int height) {
		boolean[] a = new boolean[width * height];

		a[x + y * width] = true;
		if(valid(x - 1, y    , width, height)) { a[(x - 1) + (y    ) * width] = true; }
		if(valid(x + 1, y    , width, height)) { a[(x + 1) + (y    ) * width] = true; }
		if(valid(x    , y - 1, width, height)) { a[(x    ) + (y - 1) * width] = true; }
		if(valid(x    , y + 1, width, height)) { a[(x    ) + (y + 1) * width] = true; }

		return a;
	}

	private boolean valid(int x, int y, int width, int height) {
		if(x < 0 || width  <= x) { return false; }
		if(y < 0 || height <= y) { return false; }
		return true;
	}

	//
	private void gauss(boolean[][] A, boolean[] b) {
		int width  = A[0].length;
		int height = A.length;

		int row = 0;
		int col = 0;

		while(row < height && col < width) {
			int index = row;
			while(index < height && A[index][col] == false) { index += 1; }

			if(index < height) {
				if(row != index) {
					for(int x = 0; x < width; x++) {
						boolean tmp;

						tmp         = A[  row][x];
						A[  row][x] = A[index][x];
						A[index][x] = tmp;

						tmp      = b[  row];
						b[  row] = b[index];
						b[index] = tmp;
					}
				}

				for(int y = 0; y < height; y++) {
					if(y != row && A[y][col] != false) {
						for(int x = col; x < width; x++) {
							A[y][x] = A[y][x] ^ A[row][x];
						}
						b[y] = b[y] ^ b[row];
					}
				}

				row += 1;
			}
			col += 1;
		}
	}
}
