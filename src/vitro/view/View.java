package vitro.view;

import vitro.controller.*;
import java.awt.Image;

public interface View {

	public Controller controller();

	public Image getBuffer();

	public void draw();

	public void tick(double time);
}