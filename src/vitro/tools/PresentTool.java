package vitro.tools;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import vitro.*;

public class PresentTool extends JPanel implements KeyListener {
	
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Usage: 'presentTool <slideshowClass>'");
			System.exit(0);
		}

		try {
			ClassLoader loader = new URLClassLoader(new URL[]{ new URL("file://") });
			Class setupClass = loader.loadClass(args[0]);
			main((SlideShow)setupClass.newInstance());
		}
		catch (MalformedURLException e)  { e.printStackTrace(); }
		catch (ClassNotFoundException e) { e.printStackTrace(); }
		catch (InstantiationException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
	}

	public static void main(SlideShow show) {
		// all the setup and slide creation business occurs in
		// the constructor of the SlideShow implementation.

		JFrame window = new JFrame();
		Dimension preferredSize = new Dimension(1024, 768);
		PresentTool app = new PresentTool(preferredSize,show);
		window.add(app);
		window.addKeyListener(app);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setUndecorated(true);

		// pop into fullscreen mode
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice      gd = ge.getDefaultScreenDevice();
		gd.setFullScreenWindow(window);

		// try to switch to the appropriate display resolution
		if (gd.isDisplayChangeSupported()) {
			for(DisplayMode m : gd.getDisplayModes()) {
				if (m.getWidth()  != preferredSize.getWidth())  { continue; }
				if (m.getHeight() != preferredSize.getHeight()) { continue; }
				gd.setDisplayMode(m);
				break;
			}
		}

		window.pack();

		while(true) {
			app.repaint();
			app.tick();
			try { Thread.sleep(10); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}		
	}

	private static final long serialVersionUID = 1L;
	private final Dimension size;
	private final SlideShow show;

	private int slideIndex      = 0;
	private int transitionTimer = 0;

	public PresentTool(Dimension size, SlideShow show) {
		setPreferredSize(size);
		this.size = size;
		this.show = show;
	}

	public void paint(Graphics g) {
		if (transitionTimer < 1) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, (int)size.getWidth(), (int)size.getHeight());
			show.get(slideIndex).paint((Graphics2D)(g.create()), size);
		}
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, (int)size.getWidth(), (int)size.getHeight());
			show.get(slideIndex).paint((Graphics2D)(g.create()), size);
		}
	}

	private boolean wait = true;

	public void tick() {
		if (transitionTimer > 0)  {
			transitionTimer--;
		}
		else if (!wait) {
			show.get(slideIndex).tick();
		}
	}

	public void keyReleased(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { System.exit(0); }
		if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
			show.get(slideIndex).lostFocus();
			slideIndex = Math.min(show.size() - 1, slideIndex + 1);
			show.get(slideIndex).gotFocus();
			transitionTimer = 20;
			wait = true;
		}
		if (k.getKeyCode() == KeyEvent.VK_LEFT) {
			show.get(slideIndex).lostFocus();
			slideIndex = Math.max(              0, slideIndex - 1);
			show.get(slideIndex).gotFocus();
			transitionTimer = 20;
			wait = true;
		}
		if (show.get(slideIndex) instanceof HostSlide) {
			View view = ((HostSlide)(show.get(slideIndex))).view;

			if (k.getKeyCode() == KeyEvent.VK_SPACE) {
				wait = !wait;
			}
			else if (k.getKeyCode() == KeyEvent.VK_A) {
				wait = true;
				view.controller().prev();
				view.flush();
				repaint();
			}
			else if (k.getKeyCode() == KeyEvent.VK_D) {
				wait = true;
				view.controller().next();
				view.flush();
				repaint();
			}
			else if (k.getKeyCode() == KeyEvent.VK_W) {
				wait = true;
				while(view.controller().hasPrev()) {
					view.controller().prev();
				}
				view.flush();
				repaint();
			}
		}
	}

	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {}
}