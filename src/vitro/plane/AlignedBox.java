package vitro.plane;

public class AlignedBox extends Bound {
	public final Position point0;
	public final Position point1;
	
	public AlignedBox(double x0, double y0, double x1, double y1) {
		point0 = new Position(x0, y0);
		point1 = new Position(x1, y1);
	}
}
