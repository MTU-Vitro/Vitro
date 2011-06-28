package vitro;

import vitro.graph.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

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

	private AnnotationPanel annotations;

	public void dockedController(boolean docked) {
		dockedController = docked;
	}

	public void show(View view) {
		this.view = view;

		JFrame annotationWindow = new JFrame("Data Annotations");
		JLabel annotationHeading = new JLabel();
		annotationHeading.setFont(new Font("Monospaced", Font.PLAIN, 20));
		annotationHeading.setBorder(new EmptyBorder(5, 5, 0, 5));
		annotations = new AnnotationPanel(annotationWindow, annotationHeading, view.colorScheme());
		annotationWindow.setLayout(new BorderLayout());
		annotationWindow.add(annotations, BorderLayout.CENTER);
		annotationWindow.add(annotationHeading, BorderLayout.NORTH);

		buttonPrev.setColorScheme(view.colorScheme());
		buttonPlay.setColorScheme(view.colorScheme());
		buttonNext.setColorScheme(view.colorScheme());
		buttonKey.setColorScheme(view.colorScheme());
		buttonReset.setColorScheme(view.colorScheme());

		buttonPrev.addActionListener(this);
		buttonNext.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonReset.addActionListener(this);
		buttonKey.addActionListener(this);

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
		buttons.add(buttonKey);
		
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

		setResizable(false);
		pack();
		setVisible(true);

		while(true) {
			//view.draw();
			buttonPrev.setEnabled(view.controller().hasPrev());
			buttonNext.setEnabled(view.controller().hasNext());
			buttonPlay.setEnabled(view.controller().hasNext());
			if (wait) { buttonPlay.setIcon(MediaButton.PLAY);  }
			else      { buttonPlay.setIcon(MediaButton.PAUSE); }
			repaint();

			if (!wait) {
				view.tick(.01);
			}

			for(Annotation a : view.controller().annotations().keySet()) {
				if (a instanceof DataAnnotation) {
					annotations.setAnnotation((DataAnnotation)a);
				}
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
			panel.toggleKey();
		}
	}
}

class HostPanel extends JPanel {
	private final View view;
	private final int w;
	private final int h;
	private boolean showKey = false;

	private static final long serialVersionUID = 1L;

	public HostPanel(View view) {
		this.view = view;
		w = view.width();
		h = view.height();
		setPreferredSize(new Dimension(w, h));
	}

	public void toggleKey() {
		showKey = !showKey;
	}

	public void paint(Graphics g) {
		super.paint(g);
		view.draw(g);
		if (showKey) {
			view.colorScheme().drawKey(g, 10, 10);
		}
	}
}

class AnnotationPanel extends JPanel {
	private static final int margin = 10;
	private final JFrame window;
	private final JLabel heading;
	private final ColorScheme colorScheme;
	private DataAnnotation annotation;
	private DataView view;

	private static final long serialVersionUID = 1L;

	public AnnotationPanel(JFrame window, JLabel heading, ColorScheme colorScheme) {
		this.window = window;
		this.heading = heading;
		this.colorScheme = colorScheme;
	}

	public void setAnnotation(DataAnnotation annotation) {
		if (annotation == null) {
			view = null;
			window.setVisible(false);
			this.annotation = null;
		}
		else if (!annotation.equals(this.annotation)) {
			view = new DataView(annotation.data, colorScheme);
			setPreferredSize(new Dimension(view.width() + (2*margin), view.height() + (2*margin)));
			heading.setText(annotation.label);
			window.setResizable(false);
			window.pack();
			window.setVisible(true);
			window.repaint();
			this.annotation = annotation;
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.translate(margin, margin);
		if (view != null) { view.draw(g); }
	}
}
