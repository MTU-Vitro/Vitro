package vitro.tools;

import java.util.*;
import java.io.*;
import java.net.*;
import vitro.*;

public class Thunderdome {

	public static final long RUN_TIMEOUT = 5000;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: 'thunderdome <setupClass> <Agent1Class> <Agent2Class> ... <AgentNClass>'");
			System.exit(0);
		}

		System.out.println(Arrays.asList(args));

		List<Agent> agents = new ArrayList<Agent>();
		Setup setup        = null;
		try {
			ClassLoader loader = new URLClassLoader(new URL[]{ new URL("file://") });

			for(int x = 1; x < args.length; x++) {
				Class agentClass = loader.loadClass(args[x]);
				agents.add((Agent)agentClass.newInstance());
			}
			Class setupClass = loader.loadClass(args[0]);
			setup = (Setup)setupClass.newInstance();
		}
		catch (MalformedURLException e)  { e.printStackTrace(); }
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }

		Controller controller = setup.init(agents);
		setup.eval(controller, runExperiment(controller));
	}

	@SuppressWarnings("deprecation")
	static long runExperiment(Controller controller) {
		Experiment experiment = new Experiment(controller);
		Thread exThread = new Thread(experiment);

		long startTime = System.currentTimeMillis();
		exThread.start();
		try { exThread.join(RUN_TIMEOUT); }
		catch(InterruptedException e) { e.printStackTrace(); }

		// it would be possible to use interruptedExceptions
		// to manage this timeout, but the only way to ensure
		// malicious client behavior doesn't screw things up
		// is to carry a large stick:
		exThread.stop();
		long endTime = System.currentTimeMillis();

		if (!experiment.finished) { System.out.println("(experiment timed out.)"); }

		return endTime - startTime;
	}
}

class Experiment implements Runnable {
	public boolean finished = false;
	private final Controller controller;

	public Experiment(Controller controller) {
		this.controller = controller;
	}

	public void run() {
		while(controller.hasNext()) {
			controller.next();
		}
		finished = true;
	}
}