package demos.hex;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import java.awt.Color;

public class HexEye extends Host {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new HexEye();
	}

	public HexEye() {

		Hex model                       = new Hex(10, 8);
		SequentialController controller = new SequentialController(model);
		HexView view                    = new HexView(controller, 640, 480, new ColorScheme());

		controller.bind(Actor.class, new Notator());
		for(int x = 0; x < 10; x++) {
			model.locations.put(new Actor(), new Location(model,
				(int)(model.width  * Math.random()),
				(int)(model.height * Math.random())
			));
		}

		show(view);
	}
}

class Notator implements Agent<Actor>, Annotated {
	public Action choose(Actor a, Set<Action> options) { return vitro.util.Groups.any(options); }

	public Set<Annotation> annotations() {
		VectorAnnotation labels = new VectorAnnotation();
		for(int x = 0; x < 10; x++) {
			labels.dirs.put(
				new Location(null, (int)(Math.random() * 10), (int)(Math.random() * 8)),
				(int)(Math.random() * 6) * (1.0/6)
			);
		}
		return Collections.singleton((Annotation)labels);
	}
}
