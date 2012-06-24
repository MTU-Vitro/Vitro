package vitro.tools;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;
import vitro.*;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;

public abstract class SlideShow extends AbstractList<Slide> {

	private final ArrayList<Slide> store = new ArrayList<Slide>();
	public Slide get(int index)                { return store.get(index); }
	public int size()                          { return store.size(); }
	public Slide set(int index, Slide element) { return store.set(index, element); }
	public Slide remove(int index)             { return store.remove(index); }
	public void add(int index, Slide element)  { store.add(index, element); }

	public void addImage(int index, String imagePath) {
		try { add(new ImageSlide(ImageIO.read(new File(imagePath)))); }
		catch (IOException e) { System.out.println("Error file not found " + e); }
	}

	public void addImage(String imagePath) {
		addImage(size() - 1, imagePath);
	}

	public void add(int index, View view) {
		add(index, new HostSlide(view));
	}

	public void add(View view) {
		add(size() - 1, view);
	}

	public void add(String pdfPath) {

		Document document = new Document();
		try { document.setFile(pdfPath); }
		catch (PDFException e)          { System.out.println("Error parsing PDF document " + e);     }
		catch (PDFSecurityException e)  { System.out.println("Error encryption not supported " + e); }
		catch (FileNotFoundException e) { System.out.println("Error file not found " + e);           }
		catch (IOException e)           { System.out.println("Error handling PDF document " + e);    }
		
		for(int page = 0; page < document.getNumberOfPages(); page++) {
			add(new PDFSlide(document, page));
		}
	}
}

class ImageSlide extends Slide {
	private final Image image;

	public ImageSlide(Image image) {
		this.image = image;
	}

	public void paint(Graphics2D g, Dimension size) {
		g.translate(
			(size.getWidth()  - image.getWidth(null))  / 2,
			(size.getHeight() - image.getHeight(null)) / 2
		);
		g.drawImage(image, 0, 0, null);
	}
}

class PDFSlide extends Slide {
	private final Document document;
	private final int pageNumber;
	private final int width;
	private final int height;

	public PDFSlide(Document document, int pageNumber) {
		this.document   = document;
		this.pageNumber = pageNumber;
		Dimension size = document.getPageDimension(pageNumber, 0.0f).toDimension();
		this.width  = (int)size.getWidth();
		this.height = (int)size.getHeight();
	}

	public void paint(Graphics2D g, Dimension size) {
		g.translate(
			(size.getWidth()  - width)  / 2,
			(size.getHeight() - height) / 2
		);
		document.paintPage(pageNumber, g,
			org.icepdf.core.util.GraphicsRenderingHints.SCREEN,
			org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX,
			0.0f, // rotation
			1.0f  // zoom
		);
	}
}

class HostSlide extends Slide {
	private final View view;

	public HostSlide(View view, Slide.Transition transition) {
		setTransition(transition);
		this.view = view;
	}

	public HostSlide(View view) {
		this(view, Slide.Transition.None);
	}

	void paint(Graphics2D g, Dimension size) {
		g.translate(
			(size.getWidth()  - view.width())  / 2,
			(size.getHeight() - view.height()) / 2
		);
		view.draw(g);
	}

	void tick() {
		view.tick(.1);
	}

	void gotFocus() {
		
	}

	void lostFocus() {
		
	}
}