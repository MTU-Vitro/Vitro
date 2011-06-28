package vitro.plane;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Plane extends Model {

	protected final Plane model;
	
	public final double width;
	public final double height;
	public final Map<Actor, Position> positions;

	private final CollectionObserver<Actor>                      actorObserver
		= new ActorObserver();
	private final CollectionObserver<Map.Entry<Actor, Position>> positionObserver
		= new PositionObserver();
	
	public Plane(double width, double height) {
		super(new ObservableSet<Actor>());
		positions = new ObservableMap<Actor, Position>();
		
		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableMap<Actor, Position>)positions).addObserver(positionObserver);
		
		this.width  = width;
		this.height = height;
		model = this;
	}
	
	protected boolean collides(Actor actor0, Actor actor1) {
		return false;
	}
	
	private class ActorObserver implements CollectionObserver<Actor> {
		public void added(ObservableCollection sender, Actor e) {
			// If an actor is added 'Raw' we don't
			// need to assign a default location.
		}
		
		public void removed(ObservableCollection sender, Actor e) {
			((ObservableMap<Actor, Position>)positions).store().remove(e);
		}
	}
	
	private class PositionObserver implements CollectionObserver<Map.Entry<Actor, Position>> {
		public void added(ObservableCollection sender, Map.Entry<Actor, Position> e) {
			((ObservableSet<Actor>)actors).store().add(e.getKey());
		}
		
		public void removed(ObservableCollection sender, Map.Entry<Actor, Position> e) {
			((ObservableSet<Actor>)actors).store().remove(e.getKey());
		}
	}
}
