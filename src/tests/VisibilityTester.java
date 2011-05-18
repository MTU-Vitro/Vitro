package tests;

import vitro.model.*;
import vitro.model.graph.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class VisibilityTester {
	
	private class VisModel extends Graph {
		public Player createPlayer() {
			return new Player();
		}

		public class Player extends Actor {
			public VisibleNode location(int depth) {
				return new VisibleNode(model, model.getLocation(this), depth);
			}
		}
	}

	@Test
	public void testZeroDepth() {
		VisModel model = new VisModel();
		Node n1 = model.createNode();
		Node n2 = model.createNode();
		Node n3 = model.createNode();
		Node n4 = model.createNode();
		Node n5 = model.createNode();

		Edge e1 = model.createEdge(n1, n2);
		Edge e2 = model.createEdge(n2, n1);
		Edge e3 = model.createEdge(n1, n3);
		Edge e4 = model.createEdge(n2, n4);
		Edge e5 = model.createEdge(n3, n5);
		Edge e6 = model.createEdge(n4, n5);

		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		VisibleNode node = player.location(0);

		assertEquals(0, node.edges.size());
		assertEquals(0, node.actors.size());
	}

	@Test
	public void testOneDepth() {
		VisModel model = new VisModel();
		Node n1 = model.createNode();
		Node n2 = model.createNode();
		Node n3 = model.createNode();
		Node n4 = model.createNode();
		Node n5 = model.createNode();

		Edge e1 = model.createEdge(n1, n2);
		Edge e2 = model.createEdge(n2, n1);
		Edge e3 = model.createEdge(n1, n3);
		Edge e4 = model.createEdge(n2, n4);
		Edge e5 = model.createEdge(n3, n5);
		Edge e6 = model.createEdge(n4, n5);

		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		VisibleNode node = player.location(1);

		assertEquals(2, node.edges.size());

		List<Edge> edges = new ArrayList<Edge>(node.edges);

		assertEquals(0, edges.get(0).end.edges.size());
		assertEquals(0, edges.get(1).end.edges.size());
	}

	@Test
	public void testTwoDepth() {
		VisModel model = new VisModel();
		Node n1 = model.createNode();
		Node n2 = model.createNode();
		Node n3 = model.createNode();
		Node n4 = model.createNode();
		Node n5 = model.createNode();

		Edge e1 = model.createEdge(n1, n2);
		Edge e2 = model.createEdge(n2, n1);
		Edge e3 = model.createEdge(n1, n3);
		Edge e4 = model.createEdge(n2, n4);
		Edge e5 = model.createEdge(n3, n5);
		Edge e6 = model.createEdge(n4, n5);

		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		VisibleNode node = player.location(2);

		assertEquals(2, node.edges.size());
		
		List<Edge> edges1 = new ArrayList<Edge>(node.edges);
		assertEquals(3, edges1.get(1).end.edges.size() + edges1.get(0).end.edges.size());
	}
}