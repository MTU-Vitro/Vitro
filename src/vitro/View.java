package vitro;

import java.awt.Image;

public interface View {

	public Controller controller();

	public ColorScheme colorScheme();

	public Image getBuffer();

	public void draw();

	public void tick(double time);

	public void flush();
}