package vitro.view;

import java.awt.Image;

public interface View {

	public Image getBuffer();

	public void draw();

	public void tick(double time);

	public boolean done();
}