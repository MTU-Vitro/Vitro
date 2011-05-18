package tests;

import vitro.model.*;
import vitro.model.graph.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class VisibilityTester {
	
	private class VisModel extends Graph {
		public VisModel() {
			Node n1 = createNode();
			Node n2 = createNode();
			Node n3 = createNode();
			Node n4 = createNode();
			Node n5 = createNode();

			createEdge(n1, n2);
			createEdge(n2, n1);
			createEdge(n1, n3);
			createEdge(n2, n4);
			createEdge(n3, n5);
			createEdge(n4, n5);
		}

		public Player createPlayer() {
			return new Player();
		}

		public class Player extends Actor {
			public VisibleNode location(int depth) {
				return new VisibleNode(model.getLocation(this), depth);
			}
		}
	}

	@Test
	public void testZeroDepth() {
		VisModel model = new VisModel();
		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		Graph.VisibleNode node = player.location(0);

		assertEquals(0, node.edges.size());
		assertEquals(0, node.actors.size());
	}

	@Test
	public void testOneDepth() {
		VisModel model = new VisModel();
		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		Graph.VisibleNode node = player.location(1);

		assertEquals(2, node.edges.size());
		
		// this method of testing is brittle.
		// if the hashcodes for edges or nodes change,
		// it will break.
		List<Graph.Edge> edges1 = new ArrayList<Graph.Edge>(node.edges);
		assertEquals(0, edges1.get(0).end.edges.size());
		assertEquals(0, edges1.get(1).end.edges.size());
	}

	@Test
	public void testTwoDepth() {
		VisModel model = new VisModel();
		VisModel.Player player = model.createPlayer();
		model.nodes.get(0).actors.add(player);

		Graph.VisibleNode node = player.location(2);

		assertEquals(2, node.edges.size());
		
		// this method of testing is brittle.
		// if the hashcodes for edges or nodes change,
		// it will break.
		List<Graph.Edge> edges1 = new ArrayList<Graph.Edge>(node.edges);
		assertEquals(1, edges1.get(0).end.edges.size());
		assertEquals(2, edges1.get(1).end.edges.size());
	}
}