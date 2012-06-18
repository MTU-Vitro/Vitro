package vitro.tools;

import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.net.*;
import vitro.*;

public class BarterTown {
	
	private final Scenario     scenario;
	private final ControlPanel control;
	private final ViewPanel    panel;

	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("Usage: BarterTown [scenario.class] [agent-directory] [logfile]");
			System.exit(0);
		}
		if(!new File(args[1]).isDirectory()) {
			System.out.format("'%s' is not a valid directory path.%n", new File(args[1]));
			System.exit(0);
		}
		new BarterTown(args[0], args[1], args[2]);
	}

	public BarterTown(String scenarioPath, String agentsDir, String logPath) {
		List<Class> agents = new ArrayList<Class>();

		try {
			ClassLoader loader = new FileClassLoader();
			Class scenarioClass = loader.loadClass(scenarioPath);
			scenario = (Scenario)scenarioClass.newInstance();

			for(File file : new File(agentsDir).listFiles()) {
				if (file.isDirectory()) { continue; }
				System.out.format("agent: '%s'%n", file);
				agents.add(loader.loadClass(file.toString()));
			}
		}
		catch (ClassNotFoundException e) { e.printStackTrace(); throw new Error(); }
		catch (InstantiationException e) { e.printStackTrace(); throw new Error(); }
		catch (IllegalAccessException e) { e.printStackTrace(); throw new Error(); }

		// Time for some sanity-checking.
		// Make sure that we can instantiate every
		// Agent class and that they have unique names:
		Set<String> names = new HashSet<String>();
		boolean failure = false;
		for(Class c : agents) {
			String name = className(c);
			if (names.contains(name)) {
				System.out.format("Name collision- agent '%s' defined more than once.%n", name);
				failure = true;
			}
			names.add(name);
			failure |= (spawnAgent(c) == null);
		}
		if (failure) {
			System.out.println("Failed to initialize competitors.");
			System.exit(0);
		}

		scenario.setCompetitors(agents);
		panel   = new ViewPanel(640, 480);
		control = new ControlPanel(this);

		PrintWriter logger = null;
		try {
			logger = new PrintWriter(new File(logPath));
			scenario.setLog(logger);
			panel.active(true);
			run();
			panel.active(false);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally {
			if (logger != null) {
				logger.close();
			}
		}
		System.exit(0);
	}

	enum State { PreMatch, Match, PostMatch };
	private final Object interlock = new Object();
	private State state = State.PreMatch;
	boolean advance = false;
	boolean paused  = false;
	View    view    = null;
	Anim    anim    = null;

	void restore(Scanner in) {
		synchronized(interlock) {
			scenario.restore(in);
			advance = false;
			paused  = false;
			state   = State.PreMatch;
			anim    = scenario.preMatch();
		}
	}

	public void run() {
		anim = scenario.preMatch();

		while(true) {
			synchronized(interlock) {
				switch(state) {
					case PreMatch:
						drawAnim(anim);
						if (!advance) { break; }
						state = State.Match;
						view = scenario.match();
						control.showMatch(true);
					break;

					case Match:
						if (!paused) { view.tick(.1); }
						drawView(view);
						if (!advance) { break; }
						control.showMatch(false);
						drainView(view);
						paused = false;
						state = State.PostMatch;
						anim = scenario.postMatch();
					break;

					case PostMatch:
						drawAnim(anim);
						if (!advance) { break; }
						if (!scenario.next()) { return; }
						anim = scenario.preMatch();
						state = State.PreMatch;
					break;

					default:
					throw new Error("Unknown state!");
				}
				advance = false;
			}

			try { Thread.sleep(10); }
			catch(InterruptedException e) {}
		}
	}

	void drawAnim(Anim a) {
		a.tick(.1);
		synchronized(panel.graphics()) {
			a.draw(
				(Graphics2D)panel.graphics().create(),
				panel.width(),
				panel.height()
			);
		}
	}

	void drawView(View v) {
		synchronized(panel.graphics()) {
			v.draw((Graphics2D)panel.graphics().create());
		}
	}

	void drainView(View v) {
		while(v.controller().hasNext()) {
			v.controller().next();
		}
	}

	protected static String className(Class c) {
		String[] parts = c.toString().split("[.]");
		return parts[parts.length - 1];
	}

	protected static String className(Object o) {
		return className(o.getClass());
	}

	// In the future I should go back through everything and
	// use correct generics with my Class objects.
	// (John did this terrible thing on May 9th, 2012)
	@SuppressWarnings("unchecked")
	protected static Agent spawnAgent(Class c) {
		try {
			Constructor constructor = c.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (Agent)(constructor.newInstance());
		}
		catch(NoSuchMethodException e) {
			System.out.format("Agent '%s' does not provide a zero-argument constructor.%n", className(c));
		}
		catch(InvocationTargetException e) {
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		catch(InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
}

class ViewPanel extends JPanel {
	private static final long serialVersionUID = 0L;

	private final JFrame        window;
	private final BufferedImage buff; 
	private final Graphics2D    bg;
	private final int width;
	private final int height;

	private ViewPump pump = null;
	
	public ViewPanel(int width, int height) {
		this.width  = width;
		this.height = height;

		buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bg   = (Graphics2D)buff.getGraphics();
		setPreferredSize(new Dimension(width, height));
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(this);
		window.setResizable(false);
		window.pack();
	}

	public int width()           { return width;  }
	public int height()          { return height; }
	public Graphics2D graphics() { return bg; }

	public void active(boolean show) {
		window.setVisible(show);
		if (pump != null) {
			pump.run = false;
			pump = null;
		}
		if (show == true) {
			pump = new ViewPump(this);
			pump.start();
		}
	}

	public void paint(Graphics g) {
		synchronized(bg) {
			g.drawImage(buff, 0, 0, this);
		}
	}

	private static class ViewPump extends Thread {
		private final ViewPanel parent;
		public volatile boolean run = true;

		public ViewPump(ViewPanel parent) {
			this.parent = parent;
		}

		public void run() {
			while(run) {
				parent.repaint();
				try { Thread.sleep(10); }
				catch(InterruptedException e) {}
			}
		}
	}
}

class ControlPanel implements ActionListener {
	private final BarterTown root;
	private final JFrame window;

	private final JButton restore = new JButton("Restore...");
	private final JButton replay  = new JButton("Replay");
	private final JButton advance = new JButton("Next");
	private final JButton prev    = new JButton("<<");
	private final JButton next    = new JButton(">>");
	private final JButton pause   = new JButton("Pause");

	public ControlPanel(BarterTown root) {
		this.root = root;
		showMatch(false);

		restore.addActionListener(this);
		replay .addActionListener(this);
		advance.addActionListener(this);
		prev   .addActionListener(this);
		next   .addActionListener(this);
		pause  .addActionListener(this);

		JPanel competitionPanel = new JPanel();
		competitionPanel.setLayout(new BoxLayout(competitionPanel, BoxLayout.X_AXIS));
		competitionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Competition"));
		competitionPanel.add(restore);
		competitionPanel.add(advance);

		JPanel matchPanel = new JPanel();
		matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.X_AXIS));
		matchPanel.setBorder(new TitledBorder(new EtchedBorder(), "Match"));
		matchPanel.add(prev);
		matchPanel.add(pause);
		matchPanel.add(next);
		matchPanel.add(replay);

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(competitionPanel);
		controls.add(matchPanel);

		window = new JFrame("Controller");
		window.add(controls);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}

	public void showMatch(boolean vis) {
		prev.setEnabled(vis);
		next.setEnabled(vis);
		pause.setEnabled(vis);
		replay.setEnabled(vis);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == restore) {
			try {
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(window) != JFileChooser.APPROVE_OPTION) { return; }
				root.restore(new Scanner(chooser.getSelectedFile()));
				System.out.format("Restored match from log file '%s'%n", chooser.getSelectedFile());
			}
			catch(Exception ex) {
				System.out.format("Failed to restore from log.%n");
			}
		}
		if (e.getSource() == replay) {
			while(root.view.controller().hasPrev()) {
				root.view.controller().prev();
			}
			root.paused = false;
		}
		if (e.getSource() == advance) {
			root.advance = true;
		}
		if (e.getSource() == prev) {
			root.paused = true;
			root.view.controller().prev();
			root.view.flush();
			root.drawView(root.view);
		}
		if (e.getSource() == next) {
			root.paused = true;
			root.view.controller().next();
			root.view.flush();
			root.drawView(root.view);
		}
		if (e.getSource() == pause) {
			if (root.paused) {
				root.paused = false;
			}
			else {
				root.paused = true;
				root.view.flush();
				root.drawView(root.view);
			}
		}
	}
}