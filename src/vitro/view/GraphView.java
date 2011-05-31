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

	private final ReversibleMap<Node, NodeView> nodeToView = new ReversibleMap<Node, NodeView>();
	private final ReversibleMap<NodeView, Node> viewToNode = nodeToView.reverse();

	private final Set<EdgeView>  edgeViews  = new HashSet<EdgeView>();
	private final Set<ActorView> actorViews = new HashSet<ActorView>();

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
		return createNode(x, y, null);
	}

	public Node createNode(double x, double y, String label) {
		Node ret = model.createNode();
		nodeToView.put(ret, new NodeView(ret, (int)(x*width), (int)(y*height), label));
		return ret;
	}

	public Edge createEdge(Node start, Node end) {
		Edge ret = model.createEdge(start, end);
		edgeViews.add(new EdgeView(nodeToView.get(start), nodeToView.get(end)));
		return ret;
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
		actorViews.clear();
		for(Actor a : model.actors) {
			actorViews.add(new ActorView(a));
		}
		synchronized(target) {
			tg.setColor(Color.WHITE);
			tg.fillRect(0, 0, width, height);
			for(EdgeView edge : edgeViews) {
				edge.draw(tg);
			}
			for(NodeView node : viewToNode.keySet()) {
				node.draw(tg);
			}
			for(ActorView actor : actorViews) {
				actor.draw(tg);
			}
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
			g.setColor(Color.BLACK);
			g.drawLine(start.x, start.y, end.x, end.y);
		}
	}

	private class ActorView {
		public final Actor actor;
		public final Color fill;
		public int radius = 10;

		private ActorView(Actor actor) {
			this.actor = actor;
			int x = actor.getClass().hashCode();
			fill = new Color(
				(x >> 24) & 0xFF,
				(x >> 16) & 0xFF,
				(x >>  8) & 0xFF,
				128
			);
		}

		public int x() {
			return nodeToView.get(model.getLocation(actor)).x;
		}

		public int y() {
			return nodeToView.get(model.getLocation(actor)).y;
		}

		public void draw(Graphics g) {
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
}