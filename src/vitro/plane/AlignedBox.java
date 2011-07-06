package vitro.plane;

import java.awt.*;

public class AlignedBox extends Bound {
	public Position point0;
	public Position point1;
	
	public AlignedBox(double x0, double y0, double x1, double y1) {
		point0 = new Position(x0, y0);
		point1 = new Position(x1, y1);
	}

	public void draw(Graphics2D g) {
		g.drawRect((int)point0.x, (int)point0.y, (int)(point1.x - point0.x), (int)(point1.y - point1.y));
	}
}
