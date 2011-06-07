package vitro.view;

import java.awt.*;
import javax.swing.*;

public class MediaButton extends JButton {
	
	public static final int STEP_BACK    = 0;
	public static final int STEP_FORWARD = 1;
	public static final int STOP         = 2;
	public static final int PAUSE        = 3;
	public static final int PLAY         = 4;

	private static final long serialVersionUID = 1L;

	private static Image mediaControls;
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		ClassLoader loader = MediaButton.class.getClassLoader();
		mediaControls = toolkit.getImage(loader.getResource("vitro/view/mediaControls.png"));
		while(mediaControls.getWidth(null) < 1) {
			try { Thread.sleep(1); }
			catch(InterruptedException ie) {}
		}
	}

	private int id;
	private final int w;
	private final int h;

	public MediaButton(int id) {
		this.id = id;
		w = mediaControls.getWidth(null)  / 5;
		h = mediaControls.getHeight(null) / 3;

		setFocusPainted(true);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setPreferredSize(new Dimension(w, h));
	}

	public void setIcon(int id) {
		this.id = id;
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D)g;
		Drawing.configureVector(g2);
		g2.setColor(Color.GRAY);

		g.setColor(
			(!model.isEnabled()) ? (Color.LIGHT_GRAY) :
			(model.isRollover()) ? (Color.BLACK) :
			Color.GRAY
		);
		final int margin = 5;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(margin));	
		g2.drawRoundRect(3, 3, w-6, h-6, 10, 10);
		g2.setStroke(oldStroke);

		if(model.isEnabled()) {
			g2.setColor(Color.BLACK);
			g2.drawRoundRect(1, 1, w-2, h-2, 8, 8);
		}


		final int ew = w - 2*margin;
		final int eh = h - 2*margin;

		g.setColor(
			(!model.isEnabled()) ? (Color.LIGHT_GRAY) :
			(model.isRollover()) ? (Color.BLACK) :
			Color.GRAY
		);

		if (id == PLAY) {
			g2.fillPolygon(
				new int[] { ew/3 + margin, 2*ew/3 + margin,   ew/3 + margin },
				new int[] { eh/4 + margin,   eh/2 + margin, 3*eh/4 + margin },
				3
			);
		}
		else if (id == PAUSE) {
			g2.fillRect(
				2*ew/7 + margin,
				  eh/4 + margin,
				ew/7,
				eh/2
			);
			g2.fillRect(
				4*ew/7 + margin,
				  eh/4 + margin,
				ew/7,
				eh/2
			);
		}
		else if (id == STEP_BACK) {
			g2.fillRect(
				2*ew/7 + margin,
				  eh/4 + margin,
				ew/7,
				eh/2
			);
			g2.fillPolygon(
				new int[] { 3*ew/4 + margin, 3*ew/4 + margin, 3*ew/7 + margin },
				new int[] {   eh/4 + margin, 3*eh/4 + margin,   eh/2 + margin },
				3
			);
		}
		else if (id == STEP_FORWARD) {
			g2.fillRect(
				4*ew/7 + margin,
				  eh/4 + margin,
				ew/7,
				eh/2
			);
			g2.fillPolygon(
				new int[] { 1*ew/4 + margin, 1*ew/4 + margin, 4*ew/7 + margin },
				new int[] {   eh/4 + margin, 3*eh/4 + margin,   eh/2 + margin },
				3
			);
		}

		/*
		int index = 
			(!model.isEnabled()) ? 1 :
			(model.isRollover()) ? 2 :
			0
		;
		int tx =    id * w;
		int ty = index * h;

		g.drawImage(
			mediaControls,
			 0, 0, w, h,
			tx, ty, tx+w, ty+h,
			null
		);
		*/
	}
}