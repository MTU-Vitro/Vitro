package demos.slide;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class SlidePuzzleBrain implements Agent<SlidePuzzle.Gap> {

	Queue<State> path = null;

	public Action choose(SlidePuzzle.Gap actor, Set<Action> options) {
		if(path == null) {
			State initial = new State(actor.numbers() , actor.location().x, actor.location().y);
			State goal    = new State(actor.solution(), 0                 , 0                 );

			path = search(initial, goal);
		}

		State next = path.remove();
		Location location = Groups.first(actor.neighbors(new int[][] {
			{ next.gapX - actor.location().x, next.gapY - actor.location().y }
		}));

		return actor.move(location, options);
	}

	private Queue<State> search(State initial, State goal) {
		Queue<State> frontier = new LinkedList<State>();
		frontier.add(initial);

		Map<State, State> tracks = new HashMap<State, State>();
		tracks.put(initial, null);

		while(!frontier.isEmpty()) {
			State current = frontier.remove();

			if(current.equals(goal)) {
				Queue<State> path = Collections.asLifoQueue(new LinkedList<State>());
				while(current != null) {
					path.add(current);
					current = tracks.get(current);
				}
				return path;
			}

			for(State successor : current.successors()) {
				if(!tracks.keySet().contains(successor)) { 
					tracks.put(successor, current);
					frontier.add(successor);
				}
			}
		}

		return null;
	}

	private class State {
		public final int[][] board;
		public final int     gapX;
		public final int     gapY;

		private final String asString;

		public State(int[][] board, int gapX, int gapY) {
			this.board = board;
			this.gapX  = gapX;
			this.gapY  = gapY;

			String tmp = "";
			for(int y = 0; y < board.length; y++) {
				for(int x = 0; x < board[0].length; x++) {
					tmp += "" + board[y][x];
				}
			}
			this.asString = tmp;
		}

		private boolean valid(int[] delta) {
			int newX = gapX + delta[0];
			int newY = gapY + delta[1];

			if(newX < 0 || board[0].length <= newX) { return false; }
			if(newY < 0 || board.length    <= newY) { return false; }
			return true;
		}

		private State successor(int[] delta) {
			int newX = gapX + delta[0];
			int newY = gapY + delta[1];

			int[][] newBoard = new int[board.length][board[0].length];
			for(int y = 0; y < board.length; y++) {
				for(int x = 0; x < board[0].length; x++) {
					newBoard[y][x] = board[y][x];
				}
			}

			newBoard[newY][newX] = board[gapY][gapX];
			newBoard[gapY][gapX] = board[newY][newX];

			return new State(newBoard, newX, newY);
		}

		public Collection<State> successors() {
			int[][] deltas = new int[][] {
				{  0, -1 },
				{  0,  1 },
				{ -1,  0 },
				{  1,  0 }
			};

			Collection<State> successors = new ArrayList<State>();
			for(int[] delta : deltas) {
				if(valid(delta)) { successors.add(successor(delta)); }
			}
			return successors;
		}

		@Override
		public int hashCode() {
			return asString.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof State)) { return false; }
			State other = (State)o;
			return other.asString.equals(asString);
		}

		@Override
		public String toString() {
			return asString;
		}
	}
}
