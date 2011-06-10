package vitro.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Host extends JFrame implements ActionListener {

	private View view;
	private HostPanel panel;

	private final MediaButton buttonPrev  = new MediaButton(MediaButton.STEP_BACK,    90, 70);
	private final MediaButton buttonPlay  = new MediaButton(MediaButton.PLAY,         90, 70);
	private final MediaButton buttonNext  = new MediaButton(MediaButton.STEP_FORWARD, 90, 70);
	private final MediaButton buttonKey   = new MediaButton(MediaButton.KEY,          50, 40);
	private final MediaButton buttonReset = new MediaButton(MediaButton.RESET,        50, 40);

	private static final long serialVersionUID = 1L;
	private boolean dockedController = true;
	private boolean showKey = false;

	public void dockedController(boolean docked) {
		dockedController = docked;
	}

	public void show(View view) {
		this.view = view;

		buttonPrev.setColorScheme(view.colorScheme());
		buttonPlay.setColorScheme(view.colorScheme());
		buttonNext.setColorScheme(view.colorScheme());
		buttonKey.setColorScheme(view.colorScheme());
		buttonReset.setColorScheme(view.colorScheme());

		buttonPrev.addActionListener(this);
		buttonNext.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonReset.addActionListener(this);

		setTitle("Vitro Simulation Host");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(new BorderLayout());
		panel = new HostPanel(view);
		add(panel, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignOnBaseline(true); 
		buttons.setLayout(layout);
		buttons.add(buttonReset);
		buttons.add(buttonPrev);
		buttons.add(buttonPlay);
		buttons.add(buttonNext);
		if (view instanceof GraphView) {
			buttonKey.addActionListener(this);
			buttons.add(buttonKey);
		}
		buttons.setBackground(view.colorScheme().background);

		if (dockedController) {
			add(buttons, BorderLayout.SOUTH);
		}
		else {
			JFrame frame = new JFrame("Controller");
			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
			frame.add(buttons);
			frame.pack();
			frame.setResizable(false);
			frame.setVisible(true);
		}

		pack();
		setResizable(false);
		setVisible(true);

		while(true) {
			view.draw();
			buttonPrev.setEnabled(view.controller().hasPrev());
			buttonNext.setEnabled(view.controller().hasNext());
			buttonPlay.setEnabled(view.controller().hasNext());
			if (wait) { buttonPlay.setIcon(MediaButton.PLAY);  }
			else      { buttonPlay.setIcon(MediaButton.PAUSE); }
			repaint();

			if (!wait) {
				view.tick(.01);
			}

			if (!view.controller().hasNext()) {
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
			wait = !wait;
		}
		else if (e.getSource() == buttonPrev) {
			wait = true;
			view.controller().prev();
			view.flush();
			repaint();
		}
		else if (e.getSource() == buttonNext) {
			wait = true;
			view.controller().next();
			view.flush();
			repaint();
		}
		else if (e.getSource() == buttonReset) {
			wait = true;

			throw new Error("Reset is not implemented!");
			/*
			// we basically need to do this,
			// except reflectively.
			view = new GraphView(view);
			remove(panel);
			panel = new HostPanel(view);
			add(panel, BorderLayout.CENTER);
			pack();
			*/

			//view.flush();
			//repaint();
		}
		else if (e.getSource() == buttonKey) {
			showKey = !showKey;
			((GraphView)view).showKey(showKey);
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