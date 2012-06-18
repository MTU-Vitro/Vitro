package vitro.tools;

import java.awt.*;
import java.util.*;
import java.util.List;
import vitro.*;
import vitro.util.*;

public abstract class RoundRobin extends Scenario {

	protected final List<Match> matches = new ArrayList<Match>();
	protected final int perMatch;
	protected final int rounds;
	protected int matchIndex = 0;
	protected View current = null;

	public RoundRobin(int perMatch, int rounds) {
		this.perMatch = perMatch;
		this.rounds   = rounds;
	}

	public void setCompetitors(List<Class> competitors) {

		// note: this is less than ideal.
		// Can we come up with a deterministic
		// approach for arranging matches in rounds?

		if (competitors.size() % perMatch != 0) {
			throw new Error("This algorithm can't deal with uneven pairing. shoot.");
		}

		for(int x = 0; x < rounds; x++) {
			List<Class> inRound = new ArrayList<Class>(competitors);
			Collections.shuffle(inRound);
			
			while(!inRound.isEmpty()) {
				Match m = new Match();
				for(int y = 0; y < perMatch; y++) {
					m.competitors.add(inRound.remove(0));
				}
				matches.add(m);
			}
		}
	}

	public boolean next() {
		return matchIndex < matches.size();
	}

	public Anim preMatch() {
		List<Agent> competitors = new ArrayList<Agent>();
		for(Class agentClass : matches.get(matchIndex).competitors) {
			competitors.add(BarterTown.spawnAgent(agentClass));
		}
		current = match(competitors);
		return new MatchList(matches, matchIndex, perMatch);
	}

	public View match() {
		return current;
	}

	public Anim postMatch() {

		for(Map.Entry<Agent, Integer> e : evaluate(current).entrySet()) {
			boolean found = false;
			for(Class c : matches.get(matchIndex).competitors) {
				if (c.isInstance(e.getKey())) {
					matches.get(matchIndex).scores.put(
						c,
						e.getValue()
					);
					found = true;
					break;
				}
			}
			if (!found) {
				throw new Error("Agent type/instance mismatch!");
			}
		}

		matchIndex++;
		return new Scoreboard(matches, rounds);
	}

	public void restore(Scanner in) {
		throw new Error("Not implemented!");
	}

	protected abstract View match(List<Agent> competitors);
	protected abstract Map<Agent, Integer> evaluate(View m);
}

class Match {
	Set<Class>     competitors = new HashSet<Class>();
	Map<Class, Integer> scores = new HashMap<Class, Integer>();
}

class MatchList implements Anim {
	private final List<Match> matches = new ArrayList<Match>();
	private final int index;
	private final int perMatch;
	private double sofar;

	public MatchList(List<Match> matches, int index, int perMatch) {
		this.matches.addAll(matches);
		this.index    = index;
		this.perMatch = perMatch;
	}

	public void draw(Graphics2D g, int width, int height) {
		int hspacing = width / perMatch;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		for(int y = 0; y < matches.size(); y++) {
			g.translate(0, 25);
			if      (y <  index) { g.setColor(Color.BLACK); }
			else if (y == index) { g.setColor(Color.RED);   }
			else                 { g.setColor(Color.GRAY);  }

			List<String> competitors = new ArrayList<String>();
			for(Class c : matches.get(y).competitors) {
				competitors.add(BarterTown.className(c));
			}
			Collections.sort(competitors);

			for(int x = 0; x < competitors.size(); x++) {
				g.drawString(
					competitors.get(x),
					20 + (x * hspacing),
					0
				);
			}
		}
	}

	public void tick(double time) {
		sofar += time;
	}

	public boolean done() {
		return sofar >= 20;
	}
}

class Scoreboard implements Anim {
	private final List<Score> scores = new ArrayList<Score>();
	private final int rounds;
	private double sofar;

	public Scoreboard(List<Match> matches, int rounds) {
		this.rounds = rounds;

		Map<String, Score> scoreMap = new HashMap<String, Score>();
		for(Match m : matches) {
			for(Class c : m.competitors) {
				String teamName = BarterTown.className(c);
				if (!scoreMap.containsKey(teamName)) {
					Score ns = new Score(teamName);
					scoreMap.put(teamName, ns);
					scores.add(ns);
				}
			}

			if (m.scores.size() < 1) { continue; }
			for(Map.Entry<Class, Integer> e : m.scores.entrySet()) {
				String teamName = BarterTown.className(e.getKey());
				Score targetScore = scoreMap.get(teamName);
				targetScore.scores.add(e.getValue());
			}
		}

		Collections.sort(scores);
	}

	public void draw(Graphics2D g, int width, int height) {

		int nameMaxWidth  = Drawing.stringWidth(g, "Team Name:");
		int scoreMaxWidth = 40;
		for(Score s : scores) {
			nameMaxWidth = Math.max(nameMaxWidth, Drawing.stringWidth(g, s.teamName));
			for(int v : s.scores) {
				scoreMaxWidth = Math.max(scoreMaxWidth, Drawing.stringWidth(g, String.format("%d", v)));
			}
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		// draw headings
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, width, 40);
		g.setColor(Color.BLACK);
		g.drawString(
			"Team Name:",
			20,
			30
		);
		g.drawString(
			"Scores:",
			20 + nameMaxWidth + 20,
			30
		);
		g.drawString(
			"Total:",
			20 + nameMaxWidth + 20 + (rounds * (scoreMaxWidth + 10)),
			30
		);

		// draw scores
		g.translate(0, 60);
		for(Score s : scores) {
			g.setColor(Color.BLACK);
			g.drawString(
				s.teamName,
				20,
				15
			);
			int total = 0;
			for(int z = 0; z < s.scores.size(); z++) {
				int v = s.scores.get(z);
				total += v;
				g.drawString(
					String.format("%d", v),
					20 + nameMaxWidth + 20 + (z * (scoreMaxWidth + 10)),
					15
				);
			}
			g.setColor(Color.GRAY);
			g.drawString(
				String.format("%d", total),
				20 + nameMaxWidth + 20 + (rounds * (scoreMaxWidth + 10)),
				15
			);
			g.translate(0, 20);
		}
	}

	public void tick(double time) {
		sofar += time;
	}

	public boolean done() {
		return sofar >= 20;
	}

	private static class Score implements Comparable<Score> {
		public final String teamName;
		public final List<Integer> scores = new ArrayList<Integer>();

		public Score(String name) {
			this.teamName = name;
		}

		public int compareTo(Score o) {
			return teamName.compareTo(o.teamName);
		}

		public boolean equals(Object o) {
			if (!(o instanceof Score)) { return false; }
			return teamName.equals(((Score)o).teamName);
		}

		public int hashCode() {
			return teamName.hashCode();
		}
	}
}