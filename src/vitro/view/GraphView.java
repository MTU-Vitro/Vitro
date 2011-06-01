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

	private final Map<Class, Color> palette = new HashMap<Class, Color>();
	private boolean showKey = false;

	public GraphView(Graph model, Controller controller, int width, int height) {
		this.model = model;
		this.controller = controller;
		this.width = width;
		this.height = height;
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

	private static int stringWidth(String s, Graphics g) {
		Font font = g.getFont();
		return (int)font.getStringBounds(s, g.getFontMetrics().getFontRenderContext()).getWidth();
	}

	public static void drawStringCentered(String s, int x, int y, Graphics g) {
		Font font = g.getFont();
		Rectangle2D bounds = font.getStringBounds(s, g.getFontMetrics().getFontRenderContext());
		g.drawString(
			s,
			x-((int)bounds.getWidth()/2),
			y+((int)bounds.getHeight()/2)
		);
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
			tg.setColor(Color.WHITE);
			tg.fillRect(0, 0, width, height);

			if (tg instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D)tg;
				// moar pixels:
				//g2.setRenderingHint( RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);
				//g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

				// moar smoothnesses:
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
			}
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
		for(Class c : palette.keySet()) {
			String name = c.toString().split(" ")[1];
			maxWidth = Math.max(maxWidth, stringWidth(name, g));
		}
		g.setColor(Color.WHITE);
		g.fillRoundRect(x, y+7, maxWidth + 60, 24 * palette.size() + 1, 15, 15);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y+7, maxWidth + 60, 24 * palette.size() + 1, 15, 15);
		g.drawString("Key:", x+3, 18);
		y += 8;
		for(Map.Entry<Class, Color> pair : palette.entrySet()) {
			g.setColor(pair.getValue());
			g.fillRoundRect(x+5, y+4, 40, 16, 8, 8);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x+5, y+4, 40, 16, 8, 8);
			String name = pair.getKey().toString().split(" ")[1];
			g.drawString(name, x+50, y+16);
			y += 24;
		}
	}

	public Image getBuffer() {
		synchronized(target) {
			bg.drawImage(target, 0, 0, null);
		}
		return buffer;
	}

	private static class NodeView {
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
			g.setColor(Color.WHITE);
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
			g.setColor(Color.BLACK);
			g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
			drawStringCentered(label, x, y + radius + 5, g);
		}
	}

	private static class EdgeView {
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

			g.setColor(new Color(150, 150, 150));
			g.drawLine(start.x, start.y, end.x, end.y);
			//g.setColor(Color.BLACK);
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
			if (palette.containsKey(actorClass)) {
				fill = palette.get(actorClass);
			}
			else {
				int x = actor.getClass().hashCode();
				fill = new Color(
					((x >> 24) & 0xF0) | ((x >>  0) & 0x0F),
					((x >> 16) & 0xF0) | ((x >>  8) & 0x0F),
					((x >>  8) & 0xF0) | ((x >> 16) & 0x0F),
					128
				);
				palette.put(actorClass, fill);
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
			g.setColor(fill);
			g.fillOval(x()-radius, y()-radius, 2*radius, 2*radius);
			g.setColor(Color.BLACK);
			g.drawOval(x()-radius, y()-radius, 2*radius, 2*radius);
		}
	}

	private static class Arrow {
		public double headWidth;
		public double headLength;
		public double width;
		public Color fill;
		public Color outline;
		public int startx;
		public int starty;
		public int endx;
		public int endy;

		public void draw(Graphics g) {
			
		}
	}
	
	public Node[][] layoutGrid(int xSize, int ySize) {
		Node[][] array = new Node[xSize][ySize];
		
		for(int y = 0; y < ySize; y++) {
			for(int x = 0; x < xSize; x++) {
				array[x][y] = createNode((double)(x + 1) / (xSize + 1), (double)(y + 1) / (ySize + 1), "");
			}
		}
		
		return array;
	}
}
