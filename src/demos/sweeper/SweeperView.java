package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class SweeperView implements View {

	protected final Sweeper     model;
	protected final Controller  controller;
	protected final ColorScheme colors;

	protected final int width;
	protected final int height;
	//protected final int cellSize;
	//protected final int cellMargin;
	//protected final int horizontalMargin;
	//protected final int verticalMargin;
	
	protected final Color background = new Color(198, 196, 198);
	protected final Color darker     = new Color(132, 130, 132);
	protected final Color brighter   = new Color(255, 255, 255);
	
	protected final int cellSize;
	protected final int buffer;
	
	protected final Rectangle outer;
	protected final Rectangle score;
	protected final Rectangle board;
	
	protected final Rectangle mineDisplay;
	protected final Rectangle timeDisplay;
	protected final Rectangle smiles;
	
	protected final Image faces;
	protected enum Smiles { 
		HAPPY     (0), 
		SURPRISED (1), 
		DEAD      (2), 
		COOL      (3);
		
		public final int index;
		private Smiles(int index) {
			this.index = index;
		}
	};
	
	protected final Image numbers;
	
	protected final Color[] mineColors = {
		background,
		new Color(   0,   0, 255),
		new Color(   0, 130,   0),
		new Color( 255,   0,   0),
		new Color(   0,   0, 132),
		new Color( 132,   0,   0),
		new Color(   0, 132, 132),
		new Color( 132,   0, 132),
		new Color(   0,   0,   0)
	};

	public SweeperView(Sweeper model, Controller controller) {
		this.model      = model;
		this.controller = controller;
		this.colors     = new ColorScheme(Color.BLACK, Color.GRAY, Color.WHITE);
		
		this.cellSize   = 16;
		this.buffer     = 10;
		
		this.width      = (cellSize + 1) * model.width  + 2 * buffer + 15;
		this.height     = (cellSize + 1) * model.height + 3 * buffer + 41 + 15;
		
		// main background bezeled rectangles
		this.outer = new Rectangle(
			5, 5, 
			width - 10, height - 10
		);
		this.score = new Rectangle(
			outer.x + buffer, outer.y + buffer, 
			outer.width - 2 * buffer, 41
		);
		this.board = new Rectangle(
			outer.x + buffer, score.y + score.height + buffer,
			outer.width - 2 * buffer, outer.height - score.height - 3 * buffer
		);
		
		// supplementary background bezeled rectangles
		this.timeDisplay = new Rectangle(
			score.x + score.width - 48, score.y + 8,
			40, 24
		);
		this.mineDisplay = new Rectangle(
			score.x + 8, score.y + 8,
			40, 24
		);
		this.smiles = new Rectangle(
			score.x + (score.width / 2) - 13, score.y + (score.height / 2) - 13,
			27, 27
		);
		
		// image resources
		try {
			ClassLoader loader = SweeperView.class.getClassLoader();
			this.faces   = ImageIO.read(loader.getResource("demos/sweeper/faces.png"));
			this.numbers = ImageIO.read(loader.getResource("demos/sweeper/numbers.png"));
		}
		catch(IOException e) {
			throw new Error("Unable to load image resources.");
		}
	}
	
	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }
	
	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if (sofar > .10) {
			controller.next();
			sofar = 0;
		}
	}

	public void flush() {
		
	}
	
	
	public void draw(Graphics g) {
		boolean done    = model.done();
		boolean success = model.success();
	
		drawBackground((Graphics2D)g, done, success);
		
		synchronized(model) {
			for(int y = 0; y < model.height; y++) {
				for(int x = 0; x < model.width; x++) {
					drawCell((Graphics2D) g, x, y, done, success);
				}
			}
			
			for(Actor actor : model.actors) { drawActor((Graphics2D)g, actor, done, success); }
			for(Annotation a : controller.annotations().keySet()) {
				if (a instanceof GridAnnotation) {
					drawGridAnnotation((Graphics2D)g, (GridAnnotation)a);
				}
			}
		}
	}
	
	
	
	protected void drawBackground(Graphics2D g, boolean done, boolean success) {
		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);
		
		Drawing.drawBezelRect(g, outer, 2, brighter, darker, background);
		Drawing.drawBezelRect(g, score, 2, darker, brighter, background);
		Drawing.drawBezelRect(g, board, 3, darker, brighter, background);

		drawMineDisplay(g);
		drawTimeDisplay(g);
		drawSmile(g, done, success);
	}
	
	protected void drawMineDisplay(Graphics2D g) {
		Drawing.drawBezelRect(g, mineDisplay, 1, darker, brighter, background);
		
		for(int n = 0; n < 3; n++) {
			g.drawImage(
				numbers,
				mineDisplay.x + 13 * n + 1, mineDisplay.y + 1, mineDisplay.x + 13 * n + 14, mineDisplay.y + 24,
				0, 0, 13, 23,
				null
			);
		}
	}
	
	protected void drawTimeDisplay(Graphics2D g) {
		Drawing.drawBezelRect(g, timeDisplay, 1, darker, brighter, background);
		
		for(int n = 0; n < 3; n++) {
			g.drawImage(
				numbers,
				timeDisplay.x + 13 * n + 1, timeDisplay.y + 1, timeDisplay.x + 13 * n + 14, timeDisplay.y + 24,
				0, 0, 13, 23,
				null
			);
		}
	}
	
	protected void drawSmile(Graphics2D g, boolean done, boolean success) {
		Drawing.drawBezelRect(g, smiles, 3, brighter, darker, background);
		g.draw(smiles);
		
		Smiles smile = Smiles.HAPPY;
		if(done && !success) { smile = Smiles.DEAD; }
		if(done &&  success) { smile = Smiles.COOL; }
		
		g.drawImage(
			faces,
			smiles.x + 5, smiles.y + 5, smiles.x + 22, smiles.y + 22,
			17 * smile.index, 0, 17 * (smile.index + 1), 0 + 17,
			null
		);
	}
	
	protected void drawCell(Graphics2D g, int x, int y, boolean done, boolean success) {
		g.setColor(darker);
		g.drawRect(
			board.x + 2 + x * (cellSize + 1), board.y + 2 + y * (cellSize + 1), 
			cellSize + 1, cellSize + 1
		);
		
		if(done && !success && Groups.containsType(Sweeper.Mine.class, model.actorsAt(new Location(model, x, y)))) {
			Action action = controller.previousActions().get(0);
			if(action != null && action instanceof Sweeper.FlipAction) {
				Location location = ((Sweeper.FlipAction)action).location;
			
				g.setColor(Color.RED);
				g.fillRect(
					board.x + 2 + location.x * (cellSize + 1), board.y + 2 + location.y * (cellSize + 1), 
					cellSize + 1, cellSize + 1
				);
			}
		}
		else if(model.hidden.contains(new Location(model, x, y))) {
			Rectangle cell = new Rectangle(
				board.x + 3 + x * (cellSize + 1), board.y + 3 + y * (cellSize + 1), 
				cellSize, cellSize
			);
			Drawing.drawBezelRect(g, cell, 2, brighter, darker, background);
		}
		else {
			int count = model.count(new Location(model, x, y));
			if(count < 0) { return; }
		
			g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
			g.setColor(mineColors[count]);
		
			int tx = board.x + 2 + x * (cellSize + 1) + (cellSize + 1) / 2;
			int ty = board.y + 3 + y * (cellSize + 1) + (cellSize + 1) / 2;
		
			Drawing.drawStringCentered(g, ""+count, tx    , ty    );
			Drawing.drawStringCentered(g, ""+count, tx + 1, ty    );
			Drawing.drawStringCentered(g, ""+count, tx    , ty + 1);
			Drawing.drawStringCentered(g, ""+count, tx + 1, ty + 1);
		}
	}
	
	protected void drawActor(Graphics2D g, Actor a, boolean done, boolean success) {
		Location location = model.locations.get(a);
		if (location == null) { return; }

		if(a instanceof Sweeper.Mine && ((done && !success) || !model.hidden.contains(location))) {
			int tx = board.x + 2 + location.x * (cellSize + 1) + (cellSize + 1) / 2;
			int ty = board.y + 3 + location.y * (cellSize + 1) + (cellSize + 1) / 2;
		
			Drawing.drawCircleCentered(g, tx, ty, 3, Color.BLACK, Color.BLACK);
		}
	}
	
	protected void drawGridAnnotation(Graphics2D g, GridAnnotation a) {
		for(Point p : a.coloring.keySet()) {
			Location location = new Location(model, p.x, p.y);
			if(location.valid()) {
			
				int bx = board.x + 2 + location.x * (cellSize + 1);
				int by = board.y + 2 + location.y * (cellSize + 1);
				
				g.setColor(colors.outline);
				g.fillRect(bx +  6, by + 13, 8, 2);
				g.fillRect(bx +  8, by + 12, 4, 1);
				g.fillRect(bx + 10, by +  9, 1, 3);
				
				g.setColor(a.coloring.get(p));
				g.fillPolygon(
					new int[] { bx + 4, bx + 11, bx + 10 },
					new int[] { by + 6, by +  4, by + 10 },
					3
				);
			}
		}
	}
}
