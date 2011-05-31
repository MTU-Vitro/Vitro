package vitro.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Host extends JFrame implements ActionListener {

	private View view;
	private HostPanel panel;

	private final MediaButton buttonPrev = new MediaButton(MediaButton.STEP_BACK);
	private final MediaButton buttonPlay = new MediaButton(MediaButton.PLAY);
	private final MediaButton buttonNext = new MediaButton(MediaButton.STEP_FORWARD);

	private static final long serialVersionUID = 1L;

	public void show(View view) {
		this.view = view;

		buttonPrev.addActionListener(this);
		buttonNext.addActionListener(this);
		buttonPlay.addActionListener(this);

		setTitle("Vitro Simulation Host");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
		panel = new HostPanel(view);
		add(panel, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(buttonPrev);
		buttons.add(buttonPlay);
		buttons.add(buttonNext);
		buttons.setBackground(Color.WHITE);
		add(buttons, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		setVisible(true);

		while(true) {
			view.draw();
			buttonPrev.setEnabled(view.controller().hasPrev());
			buttonNext.setEnabled(view.controller().hasNext());
			buttonPlay.setEnabled(view.controller().hasNext());
			repaint();

			if (!wait) {
				view.tick(.01);
			}

			if (view.done()) {
				setTitle("Vitro Simulation Host (Completed)");
				wait = true;
			}

			try { Thread.sleep(10); }
			catch(InterruptedException ie) {}
		}
	}

	private volatile boolean wait = true;

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == buttonPlay) {
			if (wait) { wait = false; buttonPlay.setIcon(MediaButton.PLAY);  }
			else      { wait = true;  buttonPlay.setIcon(MediaButton.PAUSE); }
		}
		else if (e.getSource() == buttonPrev) {
			wait = true;
			view.controller().prev();
			repaint();
		}
		else if (e.getSource() == buttonNext) {
			wait = true;
			view.controller().next();
			repaint();
		}
	}
}

class HostPanel extends JPanel {
	private final View view;
	private final int w;
	private final int h;

	private static final long serialVersionUID = 1L;

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