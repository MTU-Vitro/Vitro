package vitro.plane;


public class AlignedBox {
	public Position point0;
	public Position point1;
	
	public AlignedBox(double x0, double y0, double x1, double y1) {
		point0 = new Position(x0, y0);
		point1 = new Position(x1, y1);
	}
}
