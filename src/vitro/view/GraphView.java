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

	private boolean showKey = false;

	private GraphFrame previousFrame = null;
	private GraphFrame currentFrame  = null;

	public GraphView(Graph model, Controller controller, int width, int height) {
		this.model = model;
		this.controller = controller;
		this.width = width;
		this.height = height;

		palette = new ColorScheme();
		//palette = new ColorScheme(Color.RED, new Color(100, 0, 0), Color.BLACK);
		//palette.inactive = new Color(70, 0, 0);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bg = buffer.getGraphics();
		tg = target.getGraphics();
	}

	public ColorScheme colorScheme() {
		return palette;
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

	public Node[][] layoutGrid(int xSize, int ySize) {
		Node[][] array = new Node[xSize][ySize];
		
		for(int y = 0; y < ySize; y++) {
			for(int x = 0; x < xSize; x++) {
				array[x][y] = createNode((double)(x + 1) / (xSize + 1), (double)(y + 1) / (ySize + 1));
			}
		}
		
		return array;
	}

	public void showKey(boolean show) {
		showKey = show;
	}
	
	public void tick(double time) {
		if (currentFrame == null) { flush(); }
		currentFrame.tick(time);
		if (currentFrame.done() && controller.hasNext()) {
			controller.next();
			synchronized(model) {
				updateViews();
				previousFrame = currentFrame;
				currentFrame = new GraphFrame(previousFrame, model);
			}
		}
	}

	public void flush() {
		synchronized(model) {
			updateViews();
			currentFrame = new GraphFrame(null, model);
		}
	}

	public boolean done() {
		return (!controller.hasNext());
	}

	private void updateViews() {
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
	}

	public void draw() {
		if (currentFrame == null) { flush(); }
		synchronized(target) {
			tg.setColor(palette.background);
			tg.fillRect(0, 0, width, height);
			Drawing.configureVector(tg);
			
			synchronized(model) {
				updateViews();
				for(Edge edge : model.edges) {
					edgeToView.get(edge).draw(tg);
				}
				for(Node node : model.nodes) {
					nodeToView.get(node).draw(tg);
				}
				for(Actor actor : model.actors) {
					actorToView.get(actor).draw(tg);
				}
				for(Annotation a : controller.annotations().keySet()) {
					if (a instanceof ActorAnnotation) {
						ActorAnnotation aa = (ActorAnnotation)a;
						if (!model.actors.contains(aa.actor)) { continue; }
						actorToView.get(aa.actor).annotation(tg, aa);
					}
					if (a instanceof EdgeAnnotation) {
						EdgeAnnotation ea = (EdgeAnnotation)a;
						if (!model.edges.contains(ea.edge)) { continue; }
						edgeToView.get(ea.edge).annotation(tg, ea);
					}
				}
				if (showKey) {
					palette.drawKey(tg, 10, 18);
				}
			}
		}
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

			g.setColor(palette.inactive);
			g.drawLine(start.x, start.y, end.x, end.y);
			g.fillOval(s[0] - 4, s[1] - 4, 8, 8);
		}

		public void annotation(Graphics g, EdgeAnnotation e) {
			Graphics2D g2 = (Graphics2D)g;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(
				4,
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND,
				0,
				new float[] {8, 8},
				0
			));
			g2.setColor(palette.unique(e.label));
			g.drawLine(start.x, start.y, end.x, end.y);
			g2.setStroke(oldStroke);
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
			fill = palette.unique(actor.getClass());
		}

		public void draw(Graphics g) {
			if (!nodeToView.containsKey(model.getLocation(actor))) { return; }
			int x = currentFrame.getLocation(actor).x;
			int y = currentFrame.getLocation(actor).y;
			Drawing.drawCircleCentered(g, x, y, radius, palette.outline, fill);
		}

		public void annotation(Graphics g, ActorAnnotation a) {
			Graphics2D g2 = (Graphics2D)g;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(
				2,
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND,
				0,
				new float[] {4, 4},
				0
			));
			g2.setColor(palette.unique(a.label));
			int x = currentFrame.getLocation(actor).x;
			int y = currentFrame.getLocation(actor).y;
			int r = radius + 3;
			g2.drawOval(x-r, y-r, 2*r, 2*r);
			g2.setStroke(oldStroke);
		}
	}	

	private class GraphFrame {

		private final double FRAME_INTERVAL = 0.75;

		private GraphFrame previous;
		private final Map<Actor, Tweener> locations = new HashMap<Actor, Tweener>();

		public GraphFrame(GraphFrame previous, Graph model) {
			this.previous = previous;
			for(Actor actor : model.actors) {
				NodeView node = nodeToView.get(model.getLocation(actor));
				if (node != null) {
					setLocation(actor, new Point(node.x, node.y));
				}
			}
			locations.put(null, new Tweener(0, 1, FRAME_INTERVAL));
		}

		public void setLocation(Actor actor, Point location) {
			if (previous == null) {
				locations.put(actor, new Tweener(
					location,
					location,
					FRAME_INTERVAL
				));
			}
			else {
				locations.put(actor, new Tweener(
					previous.getLocation(actor),
					location,
					FRAME_INTERVAL
				));
			}
		}

		public void tick(double time) {
			// Once we've started animating, we want to
			// stop hanging on to the previous frame,
			// or else we'll have a big 'ol linked list
			// of these suckers floating around in memory.
			previous = null;

			for(Tweener t : locations.values()) {
				t.tick(time);
			}
		}

		public boolean done() {
			for(Tweener t : locations.values()) {
				if (!t.done()) { return false; }
			}
			return true;
		}

		public Point getLocation(Actor actor) {
			if (locations.containsKey(actor)) {
				return locations.get(actor).position();
			}
			NodeView node = nodeToView.get(model.getLocation(actor));
			return new Point(node.x, node.y);
		}
	}
}
