package demos.polyp;

import vitro.*;

public class PolypHost extends Host {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		new PolypHost();
	}
	
	public PolypHost() {
		Polyp model        = new Polyp();
		Controller control = new SequentialController(model);
		PolypView  view    = new PolypView(model, control, 640, 480);
		
		show(view);
	}
}
