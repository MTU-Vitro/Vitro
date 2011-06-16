package demos;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import static vitro.util.Groups.*;

public class SlidePuzzle extends Grid {
	
	public final int[][] numbers;

	public SlidePuzzle(int width, int height) {
		super(width, height);
		numbers = new int[height][width];
		locations.put(new Gap(this), new Location(this, 0, 0));
		for(int index = 0; index < width * height; index++) {
			numbers[index / width][index % width] = index; 
		}
	}

	public SlidePuzzle(int[][] numbers) {
		super(numbers[0].length, numbers.length);
		this.numbers = numbers;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if (numbers[y][x] == 0) {
					locations.put(new Gap(this), new Location(this, x, y));
				}
			}
		}
	}

	public boolean done() {
		for(int index = 0; index < width * height; index++) {
			if (numbers[index / width][index % width] != index) { return false; } 
		}
		return true;
	}

	public void shuffle() {
		Gap gap = (Gap)first(actors);
		for(int x = 0; x < 1000; x++) {
			any(gap.actions()).apply();
		}
	}

	public class Slide extends MoveAction {
		public final SlidePuzzle puzzle;
		public final Gap gap;

		public Slide(SlidePuzzle puzzle, Location location, Gap gap) {
			super(puzzle, location, gap);
			this.puzzle = puzzle;
			this.gap    = gap;
		}

		private void swap(int[][] array, int x1, int y1, int x2, int y2) {
			int tmp = array[y1][x1];
			array[y1][x1] = array[y2][x2];
			array[y2][x2] = tmp;
		}

		public void apply() {
			super.apply();
			swap(puzzle.numbers, start.x, start.y, end.x, end.y);
		}

		public void undo() {
			swap(puzzle.numbers, start.x, start.y, end.x, end.y);			
			super.undo();
		}
	}

	public class Gap extends GridActor {
		public Gap(Grid model) {
			super(model);
		}

		public int[][] numbers() {
			return numbers;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Location location : neighbors(ORTHOGONAL)) {
				ret.add(new Slide((SlidePuzzle)model, location, this));
			}
			return ret;
		}
	}

}