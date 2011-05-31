package vitro.view;

import java.awt.*;
import javax.swing.*;

public class MediaButton extends JButton {
	
	public static final int STEP_BACK    = 0;
	public static final int STEP_FORWARD = 1;
	public static final int STOP         = 2;
	public static final int PAUSE        = 3;
	public static final int PLAY         = 4;

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
	}
}