package vitro.plane;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Plane extends Model {

	protected final Plane model;
	
	public final double width;
	public final double height;
	public final Map<Actor, Frame> frames;

	private final CollectionObserver<Actor>                   actorObserver = new ActorObserver();
	private final CollectionObserver<Map.Entry<Actor, Frame>> frameObserver = new FrameObserver();
	
	public Plane(double width, double height) {
		super(new ObservableSet<Actor>());
		frames = new ObservableMap<Actor, Frame>();
		
		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableMap<Actor, Frame>)frames).addObserver(frameObserver);
		
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
			((ObservableMap<Actor, Frame>)frames).store().remove(e);
		}
	}
	
	private class FrameObserver implements CollectionObserver<Map.Entry<Actor, Frame>> {
		public void added(ObservableCollection sender, Map.Entry<Actor, Frame> e) {
			((ObservableSet<Actor>)actors).store().add(e.getKey());
		}
		
		public void removed(ObservableCollection sender, Map.Entry<Actor, Frame> e) {
			((ObservableSet<Actor>)actors).store().remove(e.getKey());
		}
	}
}
