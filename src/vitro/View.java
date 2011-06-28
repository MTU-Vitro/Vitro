package vitro;

import java.awt.Graphics;

public interface View {

	public Controller controller();

	public ColorScheme colorScheme();

	public int width();

	public int height();

	public void draw(Graphics g);

	public void tick(double time);

	public void flush();
}