package demos.warrior;

import vitro.*;
import vitro.grid.*;

public class WarriorEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new WarriorEye();
	}

	public WarriorEye() {

		Warrior model = new Warrior(new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 5, 0, 1, 1, 4, 0 },
			{ 0, 1, 0, 1, 0, 0, 0 },
			{ 0, 1, 0, 1, 1, 2, 0 },
			{ 0, 1, 0, 1, 0, 1, 0 },
			{ 0, 1, 1, 1, 0, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0 }
		});

		Controller  controller = new SequentialController(model);
		WarriorView view       = new WarriorView(controller);
		controller.bind(Warrior.Hero.class, new RandomAgent());
		show(view);
	}
}