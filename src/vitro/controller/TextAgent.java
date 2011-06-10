package vitro;

import java.util.*;

public class TextAgent<A extends Actor> implements Agent<A> {

	public Action choose(A actor, Set<Action> options) {
		System.out.println("actor: "+actor);
		System.out.println("options: ");
		List<Action> opt = new ArrayList<Action>(options);
		for(int index = 0; index < opt.size(); index++) {
			System.out.format("%2d) %s%n", index, opt.get(index));
		}
		System.out.format("%2d) do nothing.%n", opt.size());
		opt.add(null);
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println(">");
			try {
				int choice = in.nextInt();
				return opt.get(choice);
			}
			catch(Exception e) {
				System.out.println("Invalid choice.");
			}
		}
	}
}