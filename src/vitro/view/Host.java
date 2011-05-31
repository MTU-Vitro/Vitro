package vitro.view;

import java.awt.*;
import javax.swing.*;

public class Host extends JFrame {

	private View view;
	private HostPanel panel;

	public void show(View view) {
		panel = new HostPanel(view);
		add(panel);
		setTitle("Vitro Simulation Host");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		setVisible(true);

		while(true) {
			view.draw();
			panel.repaint();
			view.tick(.01);
			if (view.done()) {
				setTitle("Vitro Simulation Host (Completed)");
				break;
			}
			try { Thread.sleep(10); }
			catch(InterruptedException ie) {}
		}
	}
}

class HostPanel extends JPanel {
	private final View view;
	private final int w;
	private final int h;

	public HostPanel(View view) {
		this.view = view;
		Image buffer = view.getBuffer();
		w = buffer.getWidth(this);
		h = buffer.getHeight(this);
		setPreferredSize(new Dimension(w, h));
	}

	public void paint(Graphics g) {
		super.paint(g);
		/*
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint( RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);
		g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(view.getBuffer(), 0, 0,   w,   h,
		                               0, 0, 320, 240, this);
		*/
		g.drawImage(view.getBuffer(), 0, 0, this);
	}
}