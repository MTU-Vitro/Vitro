package vitro.tools;

import java.awt.*;
import java.util.*;
import java.util.List;
import vitro.*;
import vitro.util.*;

public abstract class Tournament extends Scenario {
	
	protected Bracket bracket;
	protected View current;
	protected final int     perMatch;
	protected final boolean rematchOnTie;

	protected Tournament(int perMatch, boolean rematchOnTie) {
		this.perMatch     = perMatch;
		this.rematchOnTie = rematchOnTie;
	}

	public void setCompetitors(List<Class> competitors) {
		this.bracket = new Bracket(competitors, perMatch);
	}

	public boolean next() {
		boolean ret = bracket.hasNext();
		if (!ret) {
			out.println("Complete");
			out.flush();
		}
		return ret;
	}

	public View match() {
		return current;
	}

	public Anim preMatch() {
		List<Agent> competitors = new ArrayList<Agent>();
		for(Class agentClass : bracket.current()) {
			competitors.add(BarterTown.spawnAgent(agentClass));
		}
		current = match(competitors);
		return new PreAnim(competitors);
	}

	public Anim postMatch() {
		Map<Agent, Integer> scores = evaluate(current);

		int bestScore = Collections.max(scores.values());
		Set<Agent> tied = new HashSet<Agent>();

		for(Agent a : scores.keySet()) {
			if (scores.get(a) == bestScore) { tied.add(a); }
		}

		if (tied.size() > 1 && rematchOnTie) {
			System.out.println("Scores tied. Re-running match.");
			return new SlideAnim(new ScoreAnim(scores), new BracketAnim(bracket));
		}
		else if (tied.size() > 1) {
			System.out.println("Scores tied. Choosing a winner randomly.");
			// since the order of elements in the hashSet is determined by
			// the hashcode of the agents, the below procedure should choose
			// 'randomly', excluding shenanigans.
		}

		Agent bestAgent = new ArrayList<Agent>(tied).get(0);
		bracket.resolve(bestAgent);
		out.format("Match %d%n", scores.size());
		for(Map.Entry<Agent, Integer> score : scores.entrySet()) {
			out.format("\t%d\t%s%n", score.getValue(), BarterTown.className(score.getKey()));
		}
		out.println();
		out.flush();

		return new SlideAnim(new ScoreAnim(scores), new BracketAnim(bracket));
	}

	public void restore(Scanner in) {
		String command = "Reset";

		// When we restore a log file, we interpret it and evaluate the
		// tournament brackets in order. To maintain a valid match log
		// for the ensuing game, we *also* need to append the events
		// in the source log to the current log:

		while(true) {
			if ("Complete".equals(command)) {
				out.format("Complete%n%n");
				break;
			}
			else if ("Reset".equals(command)) {
				out.format("Reset%n%n");
				bracket.reset();
			}
			else if ("Match".equals(command)) {
				int count = in.nextInt();
				out.format("Match %d%n", count);

				int bestScore = Integer.MIN_VALUE;
				String bestAgent = null;
				for(; count > 0; count--) {
					int score = in.nextInt();
					String agent = in.next();
					out.format("\t%d\t%s%n", score, agent);
					if (score > bestScore) {
						bestScore = score;
						bestAgent = agent;
					}
				}
				Agent winner = null;
				for(Class c : bracket.current()) {
					if (BarterTown.className(c).equals(bestAgent)) {
						winner = BarterTown.spawnAgent(c);
					}
				}
				bracket.resolve(winner);
			}
			else {
				System.out.format("Unrecognized log command: '%s'%n", command);
				break;
			}

			if (!in.hasNext()) { break; }
			command = in.next();
		}
		out.flush();
	}

	protected abstract View match(List<Agent> competitors);
	protected abstract Map<Agent, Integer> evaluate(View m);
}

class Bracket {

	private final Node root;

	public Bracket(List<Class> agents, int perMatch) {
		root = distribute(agents, perMatch);
	}

	private Node distribute(List<Class> agents, final int perMatch) {
		Node ret = new Node();
		if (agents.size() == 1) {
			ret.winner = agents.get(0);
		}
		if (agents.size() <= perMatch) {
			ret.agents.addAll(agents);
			return ret;
		}

		List<List<Class>> buckets = new ArrayList<List<Class>>();
		for(int x = 0; x < perMatch; x++) {
			buckets.add(new ArrayList<Class>());
		}
		int bucket = 0;
		for(Class a : agents) {
			buckets.get(bucket).add(a);
			bucket = (bucket + 1) % buckets.size();
		}
		for(List<Class> inTree : buckets) {
			ret.children.add(distribute(inTree, perMatch));
		}
		return ret;
	}

	public void reset() {
		root.reset();
	}

	public boolean hasNext() {
		return root.open() != null;
	}

	public List<Class> current() {
		return root.open().agents;
	}

	public void resolve(Agent a) {
		Node head = root.open();
		for(Class c : head.agents) {
			if (c.isInstance(a)) {
				head.winner = c;
				return;
			}
		}
		throw new Error("Agent type/instance mismatch!");
	}

	public void draw(Graphics2D g, int w, int h) {
		int height   = root.height() + 1;
		int colWidth = w / height;
		g.translate(colWidth * (height-1), 0);
		root.draw(g, colWidth, h);
	}

	private static class Node {
		Class winner = null;
		final List<Node>  children = new ArrayList<Node>();
		final List<Class> agents   = new ArrayList<Class>();

		void reset() {
			if (children.size() > 0) {
				winner = null;
				agents.clear();
			}
			for(Node c : children) {
				c.reset();
			}
		}

		int height() {
			if (children.size() == 0) { return 1; }
			int ret = 0;
			for(Node c : children) {
				ret = Math.max(ret, c.height());
			}
			return ret + 1;
		}

		Node open() {
			if (winner != null)       { return null; }
			if (children.size() == 0) { return this; }

			agents.clear();
			Node best = null;
			for(Node c : children) {
				Node co = c.open();
				if (co != null) {
					if (best == null || best.height() > co.height()) {
						best = co;
					}
				}
				else { agents.add(c.winner); }
			}
			return best != null ? best : this;
		}

		void draw(Graphics2D g, int colWidth, int hLeft) {
			
			g.setColor(Color.BLACK);
			g.drawLine(0, hLeft / 2, colWidth, hLeft / 2); // center line

			if (winner != null) {
				g.drawString(BarterTown.className(winner), 10, (hLeft / 2) - 5);
			}

			if (children.size() < 1) {
				int perAgent = hLeft /  (agents.size() + 1);
				g.drawLine(0, perAgent, 0, hLeft - perAgent);

				g.translate(-colWidth, perAgent);
				for(Class a : agents) {
					g.drawLine(20, 0, colWidth, 0);
					g.drawString(BarterTown.className(a), 20, -5);
					g.translate(0, perAgent);
				}
			}
			else {
				int perChild = hLeft / children.size();
				g.drawLine(0, perChild/2, 0, hLeft - perChild/2);

				for(int y = 0; y < children.size(); y++) {
					Graphics2D rg = (Graphics2D)g.create();
					rg.translate(-colWidth, y * perChild);
					children.get(y).draw(rg, colWidth, perChild);
				}
			}
		}
	}
}

class PreAnim implements Anim {
	private final List<Agent> agents;
	private double sofar;

	public PreAnim(List<Agent> agents) {
		this.agents = agents;
	}

	public void tick(double time) {
		sofar += time;
	}

	public void draw(Graphics2D g, int width, int height) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.translate(20, 20);
		g.drawString("Match: ", 0, 0);
		g.translate(20, 20);
		for(Agent a : agents) {
			g.drawString(BarterTown.className(a), 20, 0);
			g.translate(0, 15);
		}
	}

	public boolean done() {
		return sofar >= 10;
	}
}

class ScoreAnim implements Anim {
	
	private final Map<Agent, Integer> scores;
	private double sofar;

	public ScoreAnim(Map<Agent, Integer> scores) {
		this.scores = scores;
	}

	public void tick(double time) {
		sofar += time;
	}

	public void draw(Graphics2D g, int width, int height) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.translate(20, 20);
		g.drawString("Scores: ", 0, 0);
		g.translate(20, 20);
		for(Map.Entry<Agent, Integer> e : scores.entrySet()) {
			String row = String.format(
				"%s : %s",
				BarterTown.className(e.getKey()),
				e.getValue()
			);
			g.drawString(row, 0, 0);
			g.translate(0, 15);
		}
	}

	public boolean done() {
		return sofar >= 10;
	}
}

class BracketAnim implements Anim {
	private final Bracket bracket;
	private double sofar;

	public BracketAnim(Bracket bracket) {
		this.bracket = bracket;
	}

	public void tick(double time) {
		sofar += time;
	}

	public void draw(Graphics2D g, int width, int height) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		bracket.draw(g, width, height);
	}

	public boolean done() {
		return sofar >= 20;
	}
}