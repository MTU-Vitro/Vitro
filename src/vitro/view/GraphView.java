package vitro.view;

import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;


public class GraphView implements View {

	public final Graph model;
	private final Controller controller;
	private final ColorScheme palette;

	private final int width;
	private final int height;
	private final BufferedImage buffer;
	private final BufferedImage target;
	private final Graphics bg;
	private final Graphics tg;

	private final ReversibleMap<Edge, EdgeView> edgeToView = new ReversibleMap<Edge, EdgeView>();
	private final ReversibleMap<EdgeView, Edge> viewToEdge = edgeToView.reverse();

	private final ReversibleMap<Node, NodeView> nodeToView = new ReversibleMap<Node, NodeView>();
	private final ReversibleMap<NodeView, Node> viewToNode = nodeToView.reverse();

	private final ReversibleMap<Actor, ActorView> actorToView = new ReversibleMap<Actor, ActorView>();
	private final ReversibleMap<ActorView, Actor> viewToActor = actorToView.reverse();

	private final Map<Class, Color> keycolors = new HashMap<Class, Color>();
	private boolean showKey = false;

	public GraphView(Graph model, Controller controller, int width, int height) {
		this.model = model;
		this.controller = controller;
		this.width = width;
		this.height = height;

		palette = new ColorScheme();
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bg = buffer.getGraphics();
		tg = target.getGraphics();
	}

	public Controller controller() {
		return controller;
	}

	public Node createNode(double x, double y) {
		return createNode(x, y, "");
	}

	public Node createNode(double x, double y, String label) {
		Node ret = model.createNode();
		nodeToView.put(ret, new NodeView(ret, (int)(x*width), (int)(y*height), label));
		return ret;
	}

	public Edge createEdge(Node start, Node end) {
		Edge ret = model.createEdge(start, end);
		edgeToView.put(ret, new EdgeView(nodeToView.get(start), nodeToView.get(end)));
		return ret;
	}

	public void showKey(boolean show) {
		showKey = show;
	}
	
	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if (sofar >= 1) {
			if (controller.hasNext()) { controller.next(); }
			sofar = 0;
		}
	}

	public boolean done() {
		return (!controller.hasNext());
	}

	public void draw() {
		// make sure our view of the model is up-to-date:
		for(Actor actor : model.actors) {
			if (!actorToView.containsKey(actor)) {
				actorToView.put(actor, new ActorView(actor));
			}
		}
		for(Node node : model.nodes) {
			if (!nodeToView.containsKey(node)) {
				// TODO: come up with a halfway sane way to choose
				// locations for new nodes that are created raw:
				nodeToView.put(node, new NodeView(node, 0, 0, ""));
			}
		}
		for(Edge edge : model.edges) {
			if (!edgeToView.containsKey(edge)) {
				NodeView start = nodeToView.get(edge.start);
				NodeView end   = nodeToView.get(edge.end);
				edgeToView.put(edge, new EdgeView(start, end));
			}
		}
		synchronized(target) {
			tg.setColor(palette.background);
			tg.fillRect(0, 0, width, height);
			Drawing.configureVector(tg);
			synchronized(model) {
				for(Edge edge : model.edges) {
					edgeToView.get(edge).draw(tg);
				}
				for(Node node : model.nodes) {
					nodeToView.get(node).draw(tg);
				}
				for(Actor actor : model.actors) {
					actorToView.get(actor).draw(tg);
				}
				if (showKey) { drawKey(tg); }
			}
		}
	}

	private void drawKey(Graphics g) {
		int x = 10;
		int y = 18;
		int maxWidth = 0;
		for(Class c : keycolors.keySet()) {
			maxWidth = Math.max(maxWidth, Drawing.stringWidth(g, normalizedName(c)));
		}
		Drawing.drawRoundRect(
			g, x, y+7, maxWidth + 60, 24 * keycolors.size() + 1, 15,
			palette.outline,
			palette.background
		);
		g.drawString("Key:", x+3, 18);
		y += 8;
		for(Map.Entry<Class, Color> pair : keycolors.entrySet()) {
			Drawing.drawRoundRect(
				g, x+5, y+4, 40, 16, 8,
				palette.outline,
				pair.getValue()
			);
			g.drawString(normalizedName(pair.getKey()), x+50, y+16);
			y += 24;
		}
	}

	private String normalizedName(Class c) {
		String name = c.toString();
		if (name.indexOf(' ') >= 0 && name.indexOf(' ') < name.length()-1) {
			name = name.substring(name.lastIndexOf(' ')+1);
		}
		if (name.indexOf('$') >= 0 && name.indexOf('$') < name.length()-1) {
			name = name.substring(name.lastIndexOf('$')+1);
		}
		return name;
	}

	public Image getBuffer() {
		synchronized(target) {
			bg.drawImage(target, 0, 0, null);
		}
		return buffer;
	}

	private class NodeView {
		public final Node node;
		public int x;
		public int y;
		public int radius = 40;
		public String label;
		
		private NodeView(Node node, int x, int y, String label) {
			this.node = node;
			this.x = x;
			this.y = y;
			this.label = label;
		}

		public void draw(Graphics g) {
			Drawing.drawCircleCentered(g, x, y, radius, palette.outline, palette.background);
			Drawing.drawStringCentered(g, label, x, y + radius + 5);
		}
	}

	private class EdgeView {
		private final NodeView start;
		private final NodeView end;

		private EdgeView(NodeView start, NodeView end) {
			this.start = start;
			this.end = end;
		}

		public void draw(Graphics g) {
			// get desired parametric displacement
			int[] s = { end.x - start.x, end.y - start.y };
			double t = 1 - (end.radius / Math.sqrt(s[0] * s[0] + s[1] * s[1]));
			
			s[0] = start.x + (int)Math.round(s[0] * t);
			s[1] = start.y + (int)Math.round(s[1] * t);

			//g.setColor(new Color(150, 150, 150));
			g.setColor(palette.secondary);
			g.drawLine(start.x, start.y, end.x, end.y);
			g.fillOval(s[0] - 4, s[1] - 4, 8, 8);
		}
	}

	private class ActorView {
		public final Actor actor;
		public final Color fill;
		public int radius = 10;

		private ActorView(Actor actor) {
			if (actor instanceof GraphActor) {
				radius = 14;
			}
			this.actor = actor;

			Class actorClass = actor.getClass();
			if (keycolors.containsKey(actorClass)) {
				fill = keycolors.get(actorClass);
			}
			else {
				int x = actor.getClass().hashCode();
				fill = new Color(
					((x >> 24) & 0xF0) | ((x >>  0) & 0x0F),
					((x >> 16) & 0xF0) | ((x >>  8) & 0x0F),
					((x >>  8) & 0xF0) | ((x >> 16) & 0x0F),
					128
				);
				keycolors.put(actorClass, fill);
			}
		}

		public int x() {
			return nodeToView.get(model.getLocation(actor)).x;
		}

		public int y() {
			return nodeToView.get(model.getLocation(actor)).y;
		}

		public void draw(Graphics g) {
			if (!nodeToView.containsKey(model.getLocation(actor))) { return; }
			Drawing.drawCircleCentered(g, x(), y(), radius, palette.outline, fill);
		}
	}
	
	public Node[][] layoutGrid(int xSize, int ySize) {
		Node[][] array = new Node[xSize][ySize];
		
		for(int y = 0; y < ySize; y++) {
			for(int x = 0; x < xSize; x++) {
				array[x][y] = createNode((double)(x + 1) / (xSize + 1), (double)(y + 1) / (ySize + 1));
			}
		}
		
		return array;
	}
}
