package vitro;

import java.awt.Graphics2D;

public interface View {

	public Controller controller();

	public ColorScheme colorScheme();

	public int width();

	public int height();

	public void draw(Graphics2D g);

	public void tick(double time);

	public void flush();
}