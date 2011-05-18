package vitro.model.graph;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class GraphActor extends Actor {

	protected final Graph model;

	public GraphActor(Graph model) {
		this.model = model;
	}

	public Node location() {
		return model.getLocation(this);
	}

	public MoveAction move(Edge edge, Set<Action> options) {
		for(Action action : Groups.ofType(MoveAction.class, options)) {
			MoveAction move = (MoveAction)action;
			if (move.actor == this && move.edge == edge) { return move; }
		}
		return null;
	}

	public MoveAction move(Node node, Set<Action> options) {
		for(Action action : Groups.ofType(MoveAction.class, options)) {
			MoveAction move = (MoveAction)action;
			if (move.actor == this && move.edge.end == node) { return move; }
		}
		return null;
	}

	public CreateAction create(Class type, Set<Action> options) {
		for(Action action : Groups.ofType(CreateAction.class, options)) {
			CreateAction create = (CreateAction)action;
			if (type.equals(create.actor.getClass())) { return create; }
		}
		return null;
	}

	public CreateAction create(Node node, Class type, Set<Action> options) {
		for(Action action : Groups.ofType(CreateAction.class, options)) {
			CreateAction create = (CreateAction)action;
			if (type.equals(create.actor.getClass()) && node == create.node) { return create; }
		}
		return null;
	}

}