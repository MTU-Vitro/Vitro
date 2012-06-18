package vitro.tools;
import java.awt.*;

public interface Anim {
	abstract void tick(double time);
	abstract void draw(Graphics2D g, int width, int height);
	abstract boolean done();
}