package demos.polyp;

import vitro.*;
import vitro.plane.*;
import java.awt.*;

public class PolypView implements View {

	private final Polyp       model;
	private final Controller  control;
	private final int         width;
	private final int         height;

	private final ColorScheme colors;

	public PolypView(Polyp model, Controller control, int width, int height) {
		this.model   = model;
		this.control = control;
		this.width   = width;
		this.height  = height;
		
		colors = new ColorScheme(Color.BLACK, Color.PINK, Color.BLUE);
	}
	
	public Controller  controller()  { return control; }
	public ColorScheme colorScheme() { return colors;  }
	public int         width()       { return width;   }
	public int         height()      { return height;  }

	public void draw(Graphics g) {
		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);
	}
	
	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if(sofar > .50) {
			control.next();
			sofar = 0;
		}
	}
	
	public void flush() {}
}
